package com.cight.analytics;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsRepository repository;

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "analytics", key = "#repoName == null ? 'all' : #repoName")
    public AnalyticsResponse getAnalytics(String repoName) {
        String normalizedRepo = repoName == null || repoName.isBlank() ? null : repoName.trim();
        AnalyticsAggregate result = repository.aggregate(normalizedRepo);

        long success = value(result.getSuccessCount());
        long failure = value(result.getFailureCount());
        long completed = success + failure;
        Double successRate = completed == 0
                ? null
                : round((success * 100.0) / completed);
        Double average = result.getAverageDurationMs() == null
                ? null
                : round(result.getAverageDurationMs());

        return new AnalyticsResponse(
                value(result.getTotalBuilds()),
                completed,
                success,
                failure,
                value(result.getPendingCount()),
                value(result.getCancelledCount()),
                value(result.getUnknownCount()),
                successRate,
                average
        );
    }

    private static long value(Long number) {
        return number == null ? 0L : number;
    }

    private static double round(double number) {
        return BigDecimal.valueOf(number)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
