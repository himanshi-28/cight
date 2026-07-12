package com.cight.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(
        name = "cight.kafka.publisher-enabled",
        havingValue = "true",
        matchIfMissing = true
)
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelayString = "${cight.kafka.publisher-delay-ms:2000}")
    @Transactional
    public void publishPending() {
        List<OutboxEvent> events = repository.findByPublishedAtIsNullOrderByCreatedAt(
                PageRequest.of(0, 50)
        );
        for (OutboxEvent event : events) {
            try {
                kafkaTemplate.send(
                        event.getTopic(),
                        event.getMessageKey(),
                        event.getPayload()
                ).get(15, TimeUnit.SECONDS);
                event.markPublished();
            } catch (Exception exception) {
                event.markFailed(exception.getMessage());
                log.warn("Outbox publish failed for event {}", event.getId());
            }
        }
    }
}
