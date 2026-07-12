package com.cight.analysis;

import java.util.List;

public record AnalysisResult(
        String summary,
        String likelyRootCause,
        List<String> suggestedActions,
        AnalysisConfidence confidence,
        List<String> toolsUsed
) {
}
