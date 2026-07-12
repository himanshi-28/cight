package com.cight.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository repository;

    public OutboxEvent enqueue(
            String aggregateType,
            UUID aggregateId,
            String eventType,
            String topic,
            String messageKey,
            String payload
    ) {
        return repository.save(OutboxEvent.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .eventType(eventType)
                .topic(topic)
                .messageKey(messageKey)
                .payload(payload)
                .build());
    }
}
