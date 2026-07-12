package com.cight.build;

import java.time.Instant;
import java.util.UUID;

public record BuildEventResponse(
        UUID id,
        String repoName,
        String branch,
        BuildStatus status,
        String commitSha,
        String errorLog,
        Long durationMs,
        Long githubRunId,
        String workflowName,
        String runUrl,
        Instant startedAt,
        Instant completedAt,
        Instant createdAt
) {
    public static BuildEventResponse from(BuildEvent event) {
        return new BuildEventResponse(
                event.getId(),
                event.getRepoName(),
                event.getBranch(),
                event.getStatus(),
                event.getCommitSha(),
                event.getErrorLog(),
                event.getDurationMs(),
                event.getGithubRunId(),
                event.getWorkflowName(),
                event.getRunUrl(),
                event.getStartedAt(),
                event.getCompletedAt(),
                event.getCreatedAt()
        );
    }
}
