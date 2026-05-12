package com.cight.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class GitHubWebhookPayload {
    private String ref;

    private Repository repository;

    @JsonProperty("head_commit")
    private HeadCommit headCommit;

    @JsonProperty("workflow_run")
    private WorkflowRun workflowRun;

    @Data
    public static class Repository{
        @JsonProperty("full_name")
        private String fullName;
    }

    @Data
    public static class HeadCommit{
        private String id;
        private String message;
    }

    @Data
    public static class WorkflowRun{
        private String status;
        private String conclusion;

        @JsonProperty("run_duration_ms")
        private Long runDurationMs;
    }


}
