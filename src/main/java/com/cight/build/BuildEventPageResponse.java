package com.cight.build;

import org.springframework.data.domain.Page;

import java.util.List;

public record BuildEventPageResponse(
        List<BuildEventResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static BuildEventPageResponse from(Page<BuildEvent> result) {
        return new BuildEventPageResponse(
                result.getContent().stream().map(BuildEventResponse::from).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }
}
