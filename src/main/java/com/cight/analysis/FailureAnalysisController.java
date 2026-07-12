package com.cight.analysis;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FailureAnalysisController {

    private final FailureAnalysisService service;

    @PostMapping("/builds/{buildId}/analyses")
    public ResponseEntity<FailureAnalysisResponse> request(
            @PathVariable UUID buildId,
            @Valid @RequestBody AnalysisRequest request
    ) {
        FailureAnalysisResponse response = service.request(buildId, request.mode());
        return ResponseEntity.accepted()
                .location(URI.create("/api/analyses/" + response.id()))
                .body(response);
    }

    @GetMapping("/builds/{buildId}/analyses")
    public List<FailureAnalysisResponse> list(@PathVariable UUID buildId) {
        return service.listForBuild(buildId);
    }

    @GetMapping("/analyses/{id}")
    public FailureAnalysisResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping("/analyses/{id}/retry")
    public ResponseEntity<FailureAnalysisResponse> retry(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.retry(id));
    }
}
