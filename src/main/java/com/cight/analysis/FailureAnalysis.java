package com.cight.analysis;

import com.cight.build.BuildEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "failure_analyses")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FailureAnalysis {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "build_event_id", nullable = false)
    private BuildEvent buildEvent;

    @Column(nullable = false, unique = true)
    private String requestKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AnalysisMode mode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AnalysisStatus status;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String likelyRootCause;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> suggestedActions;

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private AnalysisConfidence confidence;

    private String model;
    private String promptVersion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> toolsUsed;

    private int attemptCount;
    private Long latencyMs;

    @Column(columnDefinition = "TEXT")
    private String failureReason;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
        if (suggestedActions == null) {
            suggestedActions = new ArrayList<>();
        }
        if (toolsUsed == null) {
            toolsUsed = new ArrayList<>();
        }
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    public void markRunning() {
        status = AnalysisStatus.RUNNING;
        attemptCount++;
        failureReason = null;
    }

    public void complete(AnalysisResult result, long elapsedMs) {
        status = AnalysisStatus.COMPLETED;
        summary = result.summary();
        likelyRootCause = result.likelyRootCause();
        suggestedActions = result.suggestedActions();
        confidence = result.confidence();
        toolsUsed = result.toolsUsed();
        latencyMs = elapsedMs;
        failureReason = null;
    }

    public void fail(String reason) {
        status = AnalysisStatus.FAILED;
        failureReason = reason == null
                ? "Analysis failed"
                : reason.substring(0, Math.min(2000, reason.length()));
    }

    public void retry() {
        status = AnalysisStatus.PENDING;
        failureReason = null;
    }
}
