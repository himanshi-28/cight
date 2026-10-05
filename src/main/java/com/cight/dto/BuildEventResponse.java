package com.cight.dto;

import com.cight.model.BuildEvent;
import com.cight.model.BuildStatus;

import java.time.LocalDateTime;

public record BuildEventResponse(
        String id,
        String repoName,
        String branch,
        BuildStatus status,
        String commitSha,
        Long duration,
        LocalDateTime createdAt
) {
    public static BuildEventResponse from(BuildEvent buildEvent) {
        return new BuildEventResponse(
                buildEvent.getId(),
                buildEvent.getRepoName(),
                buildEvent.getBranch(),
                buildEvent.getStatus(),
                buildEvent.getCommitSha(),
                buildEvent.getDuration(),
                buildEvent.getCreatedAt()
        );
    }
}
