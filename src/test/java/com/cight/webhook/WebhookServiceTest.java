package com.cight.webhook;

import com.cight.build.BuildEvent;
import com.cight.build.BuildEventRepository;
import com.cight.build.BuildEventService;
import com.cight.build.BuildStatus;
import com.cight.build.WebhookBuildCommand;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WebhookServiceTest {

    private final BuildEventRepository repository = mock(BuildEventRepository.class);
    private final BuildEventService buildService = mock(BuildEventService.class);
    private final WebhookService service = new WebhookService(repository, buildService);

    @Test
    void mapsCompletedFailureAndComputesDuration() {
        Instant started = Instant.parse("2026-06-27T10:00:00Z");
        Instant completed = Instant.parse("2026-06-27T10:02:00Z");
        GitHubWorkflowRunPayload payload = new GitHubWorkflowRunPayload(
                "completed",
                new GitHubWorkflowRunPayload.Repository("owner/repo"),
                new GitHubWorkflowRunPayload.WorkflowRun(
                        42L,
                        "CI",
                        "completed",
                        "failure",
                        "main",
                        "abc1234",
                        "https://github.com/owner/repo/actions/runs/42",
                        started,
                        completed
                )
        );
        BuildEvent saved = BuildEvent.builder().id(UUID.randomUUID()).build();
        when(buildService.createFromWebhook(any())).thenReturn(saved);

        WebhookResult result = service.process("workflow_run", "delivery-1", payload);

        ArgumentCaptor<WebhookBuildCommand> captor =
                ArgumentCaptor.forClass(WebhookBuildCommand.class);
        verify(buildService).createFromWebhook(captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(BuildStatus.FAILURE);
        assertThat(captor.getValue().durationMs()).isEqualTo(120_000);
        assertThat(result.outcome()).isEqualTo("accepted");
    }
}
