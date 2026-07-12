package com.cight.analysis;

import com.cight.build.BuildEvent;

public interface AiAnalysisEngine {
    AnalysisResult analyze(BuildEvent build, AnalysisMode mode);
}
