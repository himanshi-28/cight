package com.cight.service;

import com.cight.dto.BuildEventRequest;
import com.cight.dto.GitHubWebhookPayload;
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

        String status = "PENDING";
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

        BuildEventRequest request = BuildEventRequest.builder()
                .repoName(payload.getRepository().getFullName())
                .branch(branch)
                .status(status)
                .commitSha(commitSha)
                .errorLog(null)
                .duration(duration)
                .build();

        buildEventService.saveBuildEvent(request);

        log.info("Processed webhook for repo: {}",
                payload.getRepository().getFullName());
    }

    private String mapConclusionToStatus(String conclusion) {

        if ("failure".equalsIgnoreCase(conclusion)) {
            return "FAILURE";
        }

        if ("success".equalsIgnoreCase(conclusion)) {
            return "SUCCESS";
        }

        return "UNKNOWN";
    }
}