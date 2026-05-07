package com.cight.service;

import com.cight.dto.BuildEventRequest;
import com.cight.model.BuildEvent;
import com.cight.repository.BuildEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BuildEventService {

    private final BuildEventRepository buildEventRepository;

    public BuildEvent saveBuildEvent(BuildEventRequest request) {
        log.info("Saving build event for repo: {}", request.getRepoName());
        BuildEvent buildEvent = BuildEvent.builder()
                .repoName(request.getRepoName())
                .branch(request.getBranch())
                .status(request.getStatus())
                .commitSha(request.getCommitSha())
                .errorLog(request.getErrorLog())
                .duration(request.getDuration())
                .build();

        BuildEvent saved = buildEventRepository.save(buildEvent);
        log.info("Saved build event with id: {}", saved.getId());
        return saved;
    }

    public List<BuildEvent> getAllBuildEvents() {
        log.info("Fetching all build events");
        return buildEventRepository.findAll();
    }

    public List<BuildEvent> getBuildEventsByRepo(String repoName) {
        log.info("Fetching build events for repo: {}", repoName);
        return buildEventRepository.findByRepoNameOrderByCreatedAtDesc(repoName);
    }
}