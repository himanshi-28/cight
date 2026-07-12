package com.cight.build;

import java.time.Instant;

public record WebhookBuildCommand(
        String repoName,
        String branch,
        BuildStatus status,
        String commitSha,
        String errorLog,
        Long durationMs,
        Long githubRunId,
        String githubDeliveryId,
        String workflowName,
        String runUrl,
        Instant startedAt,
        Instant completedAt
) {
}
