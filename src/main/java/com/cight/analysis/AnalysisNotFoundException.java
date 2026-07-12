package com.cight.analysis;

import java.util.UUID;

public class AnalysisNotFoundException extends RuntimeException {
    public AnalysisNotFoundException(UUID id) {
        super("Failure analysis not found: " + id);
    }
}
