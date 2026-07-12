package com.cight.analytics;

public interface AnalyticsAggregate {
    Long getTotalBuilds();
    Long getSuccessCount();
    Long getFailureCount();
    Long getPendingCount();
    Long getCancelledCount();
    Long getUnknownCount();
    Double getAverageDurationMs();
}
