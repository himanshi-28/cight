package com.cight.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BuildEventRequest {
    private String repoName;
    private String branch;
    private String status;
    private String commitSha;
    private String errorLog;
    private Long duration;

}