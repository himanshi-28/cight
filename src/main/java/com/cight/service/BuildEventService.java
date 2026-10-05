package com.cight.service;

import com.cight.dto.BuildEventRequest;
import com.cight.exception.BuildEventNotFoundException;
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
        log.info("Saving build event for repo: {}", request.repoName());
        BuildEvent buildEvent = BuildEvent.builder()
                .repoName(request.repoName())
                .branch(request.branch())
                .status(request.status())
                .commitSha(request.commitSha())
                .errorLog(request.errorLog())
                .duration(request.duration())
                .build();

        BuildEvent saved = buildEventRepository.save(buildEvent);
        log.info("Saved build event with id: {}", saved.getId());
        return saved;
    }

    public List<BuildEvent> getAllBuildEvents() {
        log.info("Fetching all build events");
        return buildEventRepository.findAll();
    }

    public BuildEvent getBuildEventById(String id) {
        return buildEventRepository.findById(id)
                .orElseThrow(() -> new BuildEventNotFoundException(id));
    }

    public List<BuildEvent> getBuildEventsByRepo(String repoName) {
        log.info("Fetching build events for repo: {}", repoName);
        return buildEventRepository.findByRepoNameOrderByCreatedAtDesc(repoName);
    }
}
