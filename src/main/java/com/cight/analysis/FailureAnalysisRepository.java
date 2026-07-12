package com.cight.analysis;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FailureAnalysisRepository extends JpaRepository<FailureAnalysis, UUID> {
    Optional<FailureAnalysis> findByRequestKey(String requestKey);
    List<FailureAnalysis> findByBuildEventIdOrderByCreatedAtDesc(UUID buildEventId);
    Optional<FailureAnalysis> findFirstByBuildEventIdAndStatusOrderByCreatedAtDesc(
            UUID buildEventId,
            AnalysisStatus status
    );
}
