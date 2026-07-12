package com.cight.analysis;

import com.cight.build.BuildEvent;
import com.cight.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalysisRequestService {

    private final FailureAnalysisRepository repository;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    @Value("${cight.kafka.failure-topic:build.failures.v1}")
    private String failureTopic;

    @Value("${cight.ai.model:gemini-2.5-flash}")
    private String model;

    @Value("${cight.ai.prompt-version:v1}")
    private String promptVersion;

    public FailureAnalysis requestAutomatic(BuildEvent build, AnalysisMode mode) {
        String requestKey = "auto:%s:%s:%s".formatted(build.getId(), mode, promptVersion);
        return repository.findByRequestKey(requestKey)
                .orElseGet(() -> createAndEnqueue(build, mode, requestKey));
    }

    public FailureAnalysis requestManual(BuildEvent build, AnalysisMode mode) {
        return createAndEnqueue(build, mode, "manual:" + UUID.randomUUID());
    }

    public FailureAnalysis retry(FailureAnalysis analysis) {
        if (analysis.getStatus() != AnalysisStatus.FAILED) {
            throw new IllegalStateException("Only failed analyses can be retried.");
        }
        analysis.retry();
        FailureAnalysis saved = repository.save(analysis);
        enqueue(saved);
        return saved;
    }

    private FailureAnalysis createAndEnqueue(
            BuildEvent build,
            AnalysisMode mode,
            String requestKey
    ) {
        FailureAnalysis analysis = repository.save(FailureAnalysis.builder()
                .buildEvent(build)
                .requestKey(requestKey)
                .mode(mode)
                .status(AnalysisStatus.PENDING)
                .model(model)
                .promptVersion(promptVersion)
                .build());
        enqueue(analysis);
        return analysis;
    }

    private void enqueue(FailureAnalysis analysis) {
        FailureAnalysisEvent event = new FailureAnalysisEvent(
                UUID.randomUUID(),
                1,
                Instant.now(),
                analysis.getId(),
                analysis.getBuildEvent().getId(),
                analysis.getBuildEvent().getRepoName(),
                analysis.getBuildEvent().getCommitSha(),
                analysis.getMode()
        );
        try {
            String payload = objectMapper.writeValueAsString(event);
            outboxService.enqueue(
                    "FailureAnalysis",
                    analysis.getId(),
                    "FailureAnalysisRequested",
                    failureTopic,
                    analysis.getId().toString(),
                    payload
            );
        } catch (JacksonException exception) {
            throw new IllegalStateException("Could not serialize failure analysis event.", exception);
        }
    }
}
