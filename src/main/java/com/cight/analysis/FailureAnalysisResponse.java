package com.cight.analysis;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FailureAnalysisResponse(
        UUID id,
        UUID buildEventId,
        AnalysisMode mode,
        AnalysisStatus status,
        String summary,
        String likelyRootCause,
        List<String> suggestedActions,
        AnalysisConfidence confidence,
        String model,
        String promptVersion,
        List<String> toolsUsed,
        int attemptCount,
        Long latencyMs,
        String failureReason,
        Instant createdAt,
        Instant updatedAt
) {
    public static FailureAnalysisResponse from(FailureAnalysis analysis) {
        return new FailureAnalysisResponse(
                analysis.getId(),
                analysis.getBuildEvent().getId(),
                analysis.getMode(),
                analysis.getStatus(),
                analysis.getSummary(),
                analysis.getLikelyRootCause(),
                analysis.getSuggestedActions(),
                analysis.getConfidence(),
                analysis.getModel(),
                analysis.getPromptVersion(),
                analysis.getToolsUsed(),
                analysis.getAttemptCount(),
                analysis.getLatencyMs(),
                analysis.getFailureReason(),
                analysis.getCreatedAt(),
                analysis.getUpdatedAt()
        );
    }
}
