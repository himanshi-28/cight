package com.cight.analysis;

import com.cight.build.BuildEvent;
import com.cight.build.BuildEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@ConditionalOnProperty(
        name = "cight.kafka.consumer-enabled",
        havingValue = "true",
        matchIfMissing = true
)
@RequiredArgsConstructor
@Slf4j
public class FailureAnalysisConsumer {

    private final FailureAnalysisRepository analysisRepository;
    private final BuildEventRepository buildRepository;
    private final AiAnalysisEngine analysisEngine;
    private final ObjectMapper objectMapper;

    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(delay = 10000, multiplier = 3.0),
            dltTopicSuffix = ".dlt",
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(topics = "${cight.kafka.failure-topic:build.failures.v1}")
    public void consume(String payload) {
        FailureAnalysisEvent event = deserialize(payload);
        FailureAnalysis analysis = analysisRepository.findById(event.analysisId())
                .orElseThrow(() -> new AnalysisNotFoundException(event.analysisId()));
        if (analysis.getStatus() == AnalysisStatus.COMPLETED) {
            return;
        }

        BuildEvent build = buildRepository.findById(event.buildEventId())
                .orElseThrow(() -> new IllegalStateException("Build event no longer exists."));
        analysis.markRunning();
        analysisRepository.save(analysis);

        long started = System.nanoTime();
        try {
            AnalysisResult result = analysisEngine.analyze(build, event.mode());
            long elapsedMs = (System.nanoTime() - started) / 1_000_000;
            analysis.complete(result, elapsedMs);
            analysisRepository.save(analysis);
        } catch (RuntimeException exception) {
            analysis.fail(safeMessage(exception));
            analysisRepository.save(analysis);
            throw exception;
        }
    }

    @DltHandler
    public void deadLetter(String payload) {
        FailureAnalysisEvent event = deserialize(payload);
        analysisRepository.findById(event.analysisId()).ifPresent(analysis -> {
            analysis.fail("Analysis exhausted all Kafka retry attempts.");
            analysisRepository.save(analysis);
        });
        log.error("Analysis {} moved to the dead-letter topic", event.analysisId());
    }

    private FailureAnalysisEvent deserialize(String payload) {
        try {
            return objectMapper.readValue(payload, FailureAnalysisEvent.class);
        } catch (JacksonException exception) {
            throw new IllegalArgumentException("Invalid failure analysis event.", exception);
        }
    }

    private String safeMessage(Throwable throwable) {
        String message = throwable.getMessage();
        return message == null ? throwable.getClass().getSimpleName() : message;
    }
}
