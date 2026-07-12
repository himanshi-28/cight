package com.cight.analytics;

import com.cight.build.BuildEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AnalyticsRepository extends JpaRepository<BuildEvent, UUID> {

    @Query(value = """
            SELECT
                COUNT(*) AS "totalBuilds",
                COUNT(*) FILTER (WHERE status = 'SUCCESS') AS "successCount",
                COUNT(*) FILTER (WHERE status = 'FAILURE') AS "failureCount",
                COUNT(*) FILTER (WHERE status = 'PENDING') AS "pendingCount",
                COUNT(*) FILTER (WHERE status = 'CANCELLED') AS "cancelledCount",
                COUNT(*) FILTER (WHERE status = 'UNKNOWN') AS "unknownCount",
                AVG(duration_ms) FILTER (
                    WHERE status IN ('SUCCESS', 'FAILURE') AND duration_ms IS NOT NULL
                )::double precision AS "averageDurationMs"
            FROM build_events
            WHERE (:repoName IS NULL OR repo_name = :repoName)
            """, nativeQuery = true)
    AnalyticsAggregate aggregate(@Param("repoName") String repoName);
}
