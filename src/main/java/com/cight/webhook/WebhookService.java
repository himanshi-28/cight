package com.cight.webhook;

import com.cight.build.BuildEvent;
import com.cight.build.BuildEventRepository;
import com.cight.build.BuildEventService;
import com.cight.build.BuildStatus;
import com.cight.build.WebhookBuildCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class WebhookService {

    private final BuildEventRepository repository;
    private final BuildEventService buildEventService;

    public WebhookResult process(
            String eventType,
            String deliveryId,
            GitHubWorkflowRunPayload payload
    ) {
        if ("ping".equals(eventType)) {
            return WebhookResult.ignored();
        }
        if (!"workflow_run".equals(eventType)
                || payload.workflowRun() == null
                || payload.repository() == null
                || !"completed".equalsIgnoreCase(payload.action())
                || !"completed".equalsIgnoreCase(payload.workflowRun().status())) {
            return WebhookResult.ignored();
        }
        if (repository.existsByGithubDeliveryId(deliveryId)) {
            return repository.findByGithubRunId(payload.workflowRun().id())
                    .map(build -> WebhookResult.duplicate(build.getId()))
                    .orElse(WebhookResult.ignored());
        }

        GitHubWorkflowRunPayload.WorkflowRun run = payload.workflowRun();
        Instant startedAt = run.runStartedAt();
        Instant completedAt = run.updatedAt();
        Long durationMs = startedAt == null || completedAt == null
                ? null
                : Math.max(0, Duration.between(startedAt, completedAt).toMillis());

        BuildEvent saved = buildEventService.createFromWebhook(new WebhookBuildCommand(
                required(payload.repository().fullName(), "repository.full_name"),
                required(run.headBranch(), "workflow_run.head_branch"),
                mapConclusion(run.conclusion()),
                run.headSha(),
                null,
                durationMs,
                run.id(),
                deliveryId,
                run.name(),
                run.htmlUrl(),
                startedAt,
                completedAt
        ));
        if (saved == null) {
            return WebhookResult.ignored();
        }
        return WebhookResult.accepted(saved.getId());
    }

    private BuildStatus mapConclusion(String conclusion) {
        if (conclusion == null) {
            return BuildStatus.UNKNOWN;
        }
        return switch (conclusion.toLowerCase()) {
            case "success" -> BuildStatus.SUCCESS;
            case "failure", "timed_out", "action_required" -> BuildStatus.FAILURE;
            case "cancelled", "skipped", "neutral" -> BuildStatus.CANCELLED;
            default -> BuildStatus.UNKNOWN;
        };
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required webhook field: " + field);
        }
        return value;
    }
}
