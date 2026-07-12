package com.cight.build;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record BuildEventRequest(
        @NotBlank @Size(max = 255) String repoName,
        @NotBlank @Size(max = 255) String branch,
        @NotNull BuildStatus status,
        @Size(max = 64) String commitSha,
        @Size(max = 65535) String errorLog,
        @PositiveOrZero Long durationMs
) {
}
