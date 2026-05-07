package com.cight.controller;

import com.cight.dto.BuildEventRequest;
import com.cight.model.BuildEvent;
import com.cight.service.BuildEventService;
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
    public ResponseEntity<BuildEvent> createBuildEvent(
            @RequestBody BuildEventRequest request) {
        log.info("Received request to create build event");
        BuildEvent saved = buildEventService.saveBuildEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // GET /api/builds — get all build events
    @GetMapping
    public ResponseEntity<List<BuildEvent>> getAllBuilds() {
        log.info("Received request to get all builds");
        List<BuildEvent> builds = buildEventService.getAllBuildEvents();
        return ResponseEntity.ok(builds);
    }

    // GET /api/builds/repo/{repoName} — get builds for a specific repo
    @GetMapping("/repo/{repoName}")
    public ResponseEntity<List<BuildEvent>> getBuildsByRepo(
            @PathVariable String repoName) {
        log.info("Received request to get builds for repo: {}", repoName);
        List<BuildEvent> builds = buildEventService
            .getBuildEventsByRepo(repoName);
        return ResponseEntity.ok(builds);
    }
}