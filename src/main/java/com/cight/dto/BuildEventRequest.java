package com.cight.dto;

import com.cight.model.BuildStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BuildEventRequest(
    @NotBlank(message = "Repo Name is required")
    @Size(max = 255, message = "Max size is 255 characters")
    String repoName,

    @NotBlank(message = "Branch Name is required")
    @Size(max = 255, message = "Max size is 255 characters")
    String branch,

    @NotNull(message = "status is required")
    BuildStatus status,

    String commitSha,

    String errorLog,

    @Min(0)
    Long duration
) {}
