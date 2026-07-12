package com.cight.build;

import com.cight.analysis.AnalysisMode;
import com.cight.analysis.AnalysisRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildEventService {

    private static final int MAX_PAGE_SIZE = 100;

    private final BuildEventRepository repository;
    private final AnalysisRequestService analysisRequestService;

    @Transactional
    @CacheEvict(cacheNames = "analytics", allEntries = true)
    public BuildEvent create(BuildEventRequest request) {
        BuildEvent event = BuildEvent.builder()
                .repoName(request.repoName().trim())
                .branch(request.branch().trim())
                .status(request.status())
                .commitSha(request.commitSha())
                .errorLog(request.errorLog())
                .durationMs(request.durationMs())
                .build();

        BuildEvent saved = repository.save(event);
        if (saved.getStatus() == BuildStatus.FAILURE) {
            analysisRequestService.requestAutomatic(saved, AnalysisMode.AGENTIC);
        }
        return saved;
    }

    @Transactional
    @CacheEvict(cacheNames = "analytics", allEntries = true)
    public BuildEvent createFromWebhook(WebhookBuildCommand command) {
        if (repository.existsByGithubDeliveryId(command.githubDeliveryId())) {
            return repository.findByGithubRunId(command.githubRunId()).orElse(null);
        }

        BuildEvent event = BuildEvent.builder()
                .repoName(command.repoName())
                .branch(command.branch())
                .status(command.status())
                .commitSha(command.commitSha())
                .errorLog(command.errorLog())
                .durationMs(command.durationMs())
                .githubRunId(command.githubRunId())
                .githubDeliveryId(command.githubDeliveryId())
                .workflowName(command.workflowName())
                .runUrl(command.runUrl())
                .startedAt(command.startedAt())
                .completedAt(command.completedAt())
                .build();
        try {
            BuildEvent saved = repository.saveAndFlush(event);
            if (saved.getStatus() == BuildStatus.FAILURE) {
                analysisRequestService.requestAutomatic(saved, AnalysisMode.AGENTIC);
            }
            return saved;
        } catch (DataIntegrityViolationException exception) {
            return repository.findByGithubRunId(command.githubRunId()).orElse(null);
        }
    }

    @Transactional(readOnly = true)
    public BuildEvent getRequired(UUID id) {
        return repository.findById(id).orElseThrow(() -> new BuildNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public BuildEventPageResponse find(
            String repoName,
            BuildStatus status,
            int page,
            int requestedSize
    ) {
        String normalizedRepo = normalizeOptionalRepoName(repoName);
        int size = Math.clamp(requestedSize, 1, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<BuildEvent> result;
        if (normalizedRepo != null && status != null) {
            result = repository.findByRepoNameAndStatus(normalizedRepo, status, pageable);
        } else if (normalizedRepo != null) {
            result = repository.findByRepoName(normalizedRepo, pageable);
        } else if (status != null) {
            result = repository.findByStatus(status, pageable);
        } else {
            result = repository.findAll(pageable);
        }
        return BuildEventPageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public List<BuildEventResponse> findRecentFailures(String repoName, int requestedLimit) {
        String normalizedRepo = normalizeRequiredRepoName(repoName);
        int limit = Math.clamp(requestedLimit, 1, 20);
        return repository.findTop20ByRepoNameAndStatusOrderByCreatedAtDesc(
                        normalizedRepo,
                        BuildStatus.FAILURE
                )
                .stream()
                .limit(limit)
                .map(BuildEventResponse::from)
                .toList();
    }

    private String normalizeOptionalRepoName(String repoName) {
        return repoName == null || repoName.isBlank() ? null : normalizeRequiredRepoName(repoName);
    }

    private String normalizeRequiredRepoName(String repoName) {
        if (repoName == null || repoName.isBlank() || repoName.length() > 255) {
            throw new IllegalArgumentException("Repository name must contain 1 to 255 characters.");
        }
        return repoName.trim();
    }
}
