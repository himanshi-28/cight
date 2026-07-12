package com.cight.analysis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.time.Duration;

@Component
public class GitHubCommitClient {

    private static final Pattern REPOSITORY = Pattern.compile("[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+");
    private static final Pattern SHA = Pattern.compile("[A-Fa-f0-9]{7,64}");

    private final RestClient client;

    public GitHubCommitClient(
            RestClient.Builder builder,
            @Value("${cight.github.token:}") String token
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));
        RestClient.Builder configured = builder
                .requestFactory(requestFactory)
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28");
        if (token != null && !token.isBlank()) {
            configured.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
        this.client = configured.build();
    }

    @SuppressWarnings("unchecked")
    public CommitContext getCommit(String repoName, String commitSha) {
        if (!REPOSITORY.matcher(repoName).matches() || !SHA.matcher(commitSha).matches()) {
            throw new IllegalArgumentException("Repository or commit SHA has an invalid format.");
        }
        String[] repositoryParts = repoName.split("/", 2);
        Map<String, Object> body = client.get()
                .uri(
                        "/repos/{owner}/{repository}/commits/{sha}",
                        repositoryParts[0],
                        repositoryParts[1],
                        commitSha
                )
                .retrieve()
                .body(Map.class);
        if (body == null) {
            return new CommitContext(commitSha, null, null, List.of());
        }

        Map<String, Object> commit = (Map<String, Object>) body.getOrDefault("commit", Map.of());
        Map<String, Object> author = (Map<String, Object>) commit.getOrDefault("author", Map.of());
        List<Map<String, Object>> files =
                (List<Map<String, Object>>) body.getOrDefault("files", List.of());
        List<String> changedFiles = files.stream()
                .limit(20)
                .map(file -> String.valueOf(file.get("filename")))
                .toList();
        return new CommitContext(
                String.valueOf(body.getOrDefault("sha", commitSha)),
                value(commit.get("message")),
                value(author.get("name")),
                changedFiles
        );
    }

    private String value(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    public record CommitContext(
            String sha,
            String message,
            String author,
            List<String> changedFiles
    ) {
    }
}
