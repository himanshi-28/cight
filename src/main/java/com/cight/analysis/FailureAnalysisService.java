package com.cight.analysis;

import com.cight.build.BuildEvent;
import com.cight.build.BuildEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FailureAnalysisService {

    private final FailureAnalysisRepository repository;
    private final AnalysisRequestService requestService;
    private final BuildEventService buildEventService;

    @Transactional
    public FailureAnalysisResponse request(UUID buildId, AnalysisMode mode) {
        BuildEvent build = buildEventService.getRequired(buildId);
        return FailureAnalysisResponse.from(requestService.requestManual(build, mode));
    }

    @Transactional(readOnly = true)
    public FailureAnalysis getRequired(UUID id) {
        return repository.findById(id).orElseThrow(() -> new AnalysisNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public FailureAnalysisResponse get(UUID id) {
        return FailureAnalysisResponse.from(getRequired(id));
    }

    @Transactional(readOnly = true)
    public List<FailureAnalysisResponse> listForBuild(UUID buildId) {
        buildEventService.getRequired(buildId);
        return repository.findByBuildEventIdOrderByCreatedAtDesc(buildId)
                .stream()
                .map(FailureAnalysisResponse::from)
                .toList();
    }

    @Transactional
    public FailureAnalysisResponse retry(UUID id) {
        return FailureAnalysisResponse.from(requestService.retry(getRequired(id)));
    }

    @Transactional(readOnly = true)
    public FailureAnalysisResponse latestCompleted(UUID buildId) {
        return repository.findFirstByBuildEventIdAndStatusOrderByCreatedAtDesc(
                        buildId,
                        AnalysisStatus.COMPLETED
                )
                .map(FailureAnalysisResponse::from)
                .orElse(null);
    }
}
