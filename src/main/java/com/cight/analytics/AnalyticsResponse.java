package com.cight.analytics;

import java.io.Serializable;

public record AnalyticsResponse(
        long totalBuilds,
        long completedBuilds,
        long successCount,
        long failureCount,
        long pendingCount,
        long cancelledCount,
        long unknownCount,
        Double successRatePercent,
        Double averageDurationMs
) implements Serializable {
}
