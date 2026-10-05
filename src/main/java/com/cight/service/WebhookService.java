package com.cight.service;

import com.cight.dto.BuildEventRequest;
import com.cight.dto.GitHubWebhookPayload;
import com.cight.model.BuildStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

    private final BuildEventService buildEventService;

    public void processWebhook(GitHubWebhookPayload payload) {

        String branch = payload.getRef()
                .replace("refs/heads/", "");

        BuildStatus status = BuildStatus.PENDING;
        Long duration = null;

        if (payload.getWorkflowRun() != null) {

            status = mapConclusionToStatus(
                    payload.getWorkflowRun().getConclusion()
            );

            duration = payload.getWorkflowRun()
                    .getRunDurationMs();
        }

        String commitSha = null;

        if (payload.getHeadCommit() != null) {
            commitSha = payload.getHeadCommit().getId();
        }

        BuildEventRequest request = new BuildEventRequest(
                payload.getRepository().getFullName(), branch, status,
                commitSha, null, duration);

        buildEventService.saveBuildEvent(request);

        log.info("Processed webhook for repo: {}",
                payload.getRepository().getFullName());
    }

    private BuildStatus mapConclusionToStatus(String conclusion) {

        if ("failure".equalsIgnoreCase(conclusion)) {
            return BuildStatus.FAILURE;
        }

        if ("success".equalsIgnoreCase(conclusion)) {
            return BuildStatus.SUCCESS;
        }

        return BuildStatus.UNKNOWN;
    }
}
