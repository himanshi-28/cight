package com.cight.analysis;

import com.cight.build.BuildEventRepository;
import com.cight.build.BuildStatus;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class AgentTools {

    private final BuildEventRepository repository;
    private final GitHubCommitClient gitHubCommitClient;
    private final SensitiveTextSanitizer sanitizer;
    private final ThreadLocal<List<String>> trace = ThreadLocal.withInitial(ArrayList::new);

    public AgentTools(
            BuildEventRepository repository,
            GitHubCommitClient gitHubCommitClient,
            SensitiveTextSanitizer sanitizer
    ) {
        this.repository = repository;
        this.gitHubCommitClient = gitHubCommitClient;
        this.sanitizer = sanitizer;
    }

    public void beginTrace() {
        trace.set(new ArrayList<>());
    }

    public List<String> endTrace() {
        List<String> used = List.copyOf(trace.get());
        trace.remove();
        return used;
    }

    @Tool(description = "Find recent failed CI builds for a repository to compare recurring errors.")
    @Transactional(readOnly = true)
    public List<PastFailure> queryPastFailures(
            @ToolParam(description = "GitHub repository in owner/name form") String repoName,
            @ToolParam(description = "Maximum results from 1 to 20") Integer limit
    ) {
        trace.get().add("queryPastFailures");
        String normalizedRepo = requireRepoName(repoName);
        int boundedLimit = Math.clamp(limit == null ? 5 : limit, 1, 20);
        return repository.findTop20ByRepoNameAndStatusOrderByCreatedAtDesc(
                        normalizedRepo,
                        BuildStatus.FAILURE
                )
                .stream()
                .limit(boundedLimit)
                .map(build -> new PastFailure(
                        build.getId().toString(),
                        build.getCommitSha(),
                        build.getCreatedAt(),
                        sanitizer.sanitize(build.getErrorLog(), 1000)
                ))
                .toList();
    }

    @Tool(description = "Fetch message, author, and changed file names for a GitHub commit.")
    public GitHubCommitClient.CommitContext getCommitContext(
            @ToolParam(description = "GitHub repository in owner/name form") String repoName,
            @ToolParam(description = "Commit SHA") String commitSha
    ) {
        trace.get().add("getCommitContext");
        GitHubCommitClient.CommitContext context =
                gitHubCommitClient.getCommit(requireRepoName(repoName), commitSha);
        return new GitHubCommitClient.CommitContext(
                context.sha(),
                sanitizeOptional(context.message(), 2000),
                sanitizeOptional(context.author(), 255),
                context.changedFiles().stream()
                        .limit(20)
                        .map(file -> sanitizeOptional(file, 500))
                        .toList()
        );
    }

    @Tool(description = "Calculate failure percentage among completed builds for a recent time window.")
    @Transactional(readOnly = true)
    public InstabilityScore calculateRepoInstabilityScore(
            @ToolParam(description = "GitHub repository in owner/name form") String repoName,
            @ToolParam(description = "Window in days from 1 to 365") Integer windowDays
    ) {
        trace.get().add("calculateRepoInstabilityScore");
        String normalizedRepo = requireRepoName(repoName);
        int days = Math.clamp(windowDays == null ? 30 : windowDays, 1, 365);
        Instant since = Instant.now().minus(days, ChronoUnit.DAYS);
        long success = repository.countByRepoStatusSince(
                normalizedRepo,
                BuildStatus.SUCCESS,
                since
        );
        long failure = repository.countByRepoStatusSince(
                normalizedRepo,
                BuildStatus.FAILURE,
                since
        );
        long completed = success + failure;
        Double failureRate = completed == 0 ? null : failure * 100.0 / completed;
        return new InstabilityScore(days, success, failure, failureRate);
    }

    private String requireRepoName(String repoName) {
        if (repoName == null || repoName.isBlank() || repoName.length() > 255) {
            throw new IllegalArgumentException("Repository name must contain 1 to 255 characters.");
        }
        return repoName.trim();
    }

    private String sanitizeOptional(String value, int maxCharacters) {
        return value == null ? null : sanitizer.sanitize(value, maxCharacters);
    }

    public record PastFailure(
            String buildId,
            String commitSha,
            Instant createdAt,
            String redactedErrorExcerpt
    ) {
    }

    public record InstabilityScore(
            int windowDays,
            long successCount,
            long failureCount,
            Double failureRatePercent
    ) {
    }
}
