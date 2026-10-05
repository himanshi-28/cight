package com.cight.controller;

import com.cight.dto.BuildEventRequest;
import com.cight.dto.BuildEventResponse;
import com.cight.model.BuildEvent;
import com.cight.service.BuildEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/builds")
@RequiredArgsConstructor
@Slf4j
public class BuildController {

    private final BuildEventService buildEventService;

    // POST /api/builds — save a new build event
    @PostMapping
    public ResponseEntity<BuildEventResponse> createBuildEvent(
            @Valid @RequestBody BuildEventRequest request) {
        log.info("Received request to create build event");
        BuildEvent saved = buildEventService.saveBuildEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BuildEventResponse.from(saved));
    }

    // GET /api/builds — get all build events
    @GetMapping
    public ResponseEntity<List<BuildEventResponse>> getAllBuilds() {
        log.info("Received request to get all builds");
        List<BuildEvent> builds = buildEventService.getAllBuildEvents();
        return ResponseEntity.ok(builds.stream().map(BuildEventResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuildEventResponse> getBuildById(@PathVariable String id) {
        BuildEvent build = buildEventService.getBuildEventById(id);
        return ResponseEntity.ok(BuildEventResponse.from(build));
    }

    // GET /api/builds/repo/{repoName} — get builds for a specific repo
    @GetMapping("/repo/{repoName}")
    public ResponseEntity<List<BuildEventResponse>> getBuildsByRepo(
            @PathVariable String repoName) {
        log.info("Received request to get builds for repo: {}", repoName);
        List<BuildEvent> builds = buildEventService
            .getBuildEventsByRepo(repoName);
        return ResponseEntity.ok(builds.stream().map(BuildEventResponse::from).toList());
    }
}
