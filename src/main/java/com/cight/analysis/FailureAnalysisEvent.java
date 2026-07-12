package com.cight.analysis;

import java.time.Instant;
import java.util.UUID;

public record FailureAnalysisEvent(
        UUID eventId,
        int schemaVersion,
        Instant occurredAt,
        UUID analysisId,
        UUID buildEventId,
        String repoName,
        String commitSha,
        AnalysisMode mode
) {
}
