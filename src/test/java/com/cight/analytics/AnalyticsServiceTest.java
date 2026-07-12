package com.cight.analytics;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnalyticsServiceTest {

    private final AnalyticsRepository repository = mock(AnalyticsRepository.class);
    private final AnalyticsService service = new AnalyticsService(repository);

    @Test
    void usesOnlyCompletedBuildsForSuccessRate() {
        AnalyticsAggregate aggregate = aggregate(20L, 8L, 2L, 10L, 0L, 0L, 1250.555);
        when(repository.aggregate(null)).thenReturn(aggregate);

        AnalyticsResponse response = service.getAnalytics(null);

        assertThat(response.totalBuilds()).isEqualTo(20);
        assertThat(response.completedBuilds()).isEqualTo(10);
        assertThat(response.successRatePercent()).isEqualTo(80.0);
        assertThat(response.averageDurationMs()).isEqualTo(1250.56);
    }

    @Test
    void returnsNullForUndefinedMetrics() {
        AnalyticsAggregate aggregate = aggregate(3L, 0L, 0L, 3L, 0L, 0L, null);
        when(repository.aggregate("owner/repo")).thenReturn(aggregate);

        AnalyticsResponse response = service.getAnalytics("owner/repo");

        assertThat(response.successRatePercent()).isNull();
        assertThat(response.averageDurationMs()).isNull();
    }

    private AnalyticsAggregate aggregate(
            Long total,
            Long success,
            Long failure,
            Long pending,
            Long cancelled,
            Long unknown,
            Double average
    ) {
        AnalyticsAggregate aggregate = mock(AnalyticsAggregate.class);
        when(aggregate.getTotalBuilds()).thenReturn(total);
        when(aggregate.getSuccessCount()).thenReturn(success);
        when(aggregate.getFailureCount()).thenReturn(failure);
        when(aggregate.getPendingCount()).thenReturn(pending);
        when(aggregate.getCancelledCount()).thenReturn(cancelled);
        when(aggregate.getUnknownCount()).thenReturn(unknown);
        when(aggregate.getAverageDurationMs()).thenReturn(average);
        return aggregate;
    }
}
