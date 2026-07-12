package com.cight.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubWorkflowRunPayload(
        String action,
        Repository repository,
        @JsonProperty("workflow_run") WorkflowRun workflowRun
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Repository(
            @JsonProperty("full_name") String fullName
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WorkflowRun(
            Long id,
            String name,
            String status,
            String conclusion,
            @JsonProperty("head_branch") String headBranch,
            @JsonProperty("head_sha") String headSha,
            @JsonProperty("html_url") String htmlUrl,
            @JsonProperty("run_started_at") Instant runStartedAt,
            @JsonProperty("updated_at") Instant updatedAt
    ) {
    }
}
