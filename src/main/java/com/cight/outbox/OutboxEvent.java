package com.cight.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OutboxEvent {

    @Id
    private UUID id;

    @Column(nullable = false, length = 64)
    private String aggregateType;

    @Column(nullable = false)
    private UUID aggregateId;

    @Column(nullable = false, length = 128)
    private String eventType;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String messageKey;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant publishedAt;
    private int publishAttempts;

    @Column(columnDefinition = "TEXT")
    private String lastError;

    @PrePersist
    void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    void markPublished() {
        publishedAt = Instant.now();
        lastError = null;
    }

    void markFailed(String reason) {
        publishAttempts++;
        lastError = reason == null ? "Unknown publish error" : reason.substring(0, Math.min(2000, reason.length()));
    }
}
