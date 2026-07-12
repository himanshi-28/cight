package com.cight.analysis;

import jakarta.validation.constraints.NotNull;

public record AnalysisRequest(@NotNull AnalysisMode mode) {
}
