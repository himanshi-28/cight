package com.cight.build;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/builds")
@RequiredArgsConstructor
public class BuildController {

    private final BuildEventService service;

    @PostMapping
    public ResponseEntity<BuildEventResponse> create(@Valid @RequestBody BuildEventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BuildEventResponse.from(service.create(request)));
    }

    @GetMapping
    public BuildEventPageResponse find(
            @RequestParam(required = false) String repoName,
            @RequestParam(required = false) BuildStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return service.find(repoName, status, page, size);
    }

    @GetMapping("/{id}")
    public BuildEventResponse get(@PathVariable UUID id) {
        return BuildEventResponse.from(service.getRequired(id));
    }
}
