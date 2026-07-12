package com.cight.build;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "build_events")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BuildEvent {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String repoName;

    @Column(nullable = false)
    private String branch;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private BuildStatus status;

    @Column(length = 64)
    private String commitSha;

    @Column(columnDefinition = "TEXT")
    private String errorLog;

    private Long durationMs;
    private Long githubRunId;

    @Column(length = 128)
    private String githubDeliveryId;

    private String workflowName;

    @Column(columnDefinition = "TEXT")
    private String runUrl;

    private Instant startedAt;
    private Instant completedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
