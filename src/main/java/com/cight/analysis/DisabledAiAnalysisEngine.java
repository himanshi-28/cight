package com.cight.analysis;

import com.cight.build.BuildEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "cight.ai.enabled", havingValue = "false", matchIfMissing = true)
public class DisabledAiAnalysisEngine implements AiAnalysisEngine {

    @Override
    public AnalysisResult analyze(BuildEvent build, AnalysisMode mode) {
        throw new IllegalStateException(
                "AI analysis is disabled. Set AI_ENABLED=true and provide GEMINI_API_KEY."
        );
    }
}
