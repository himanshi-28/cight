package com.cight.analysis;

import com.cight.build.BuildEvent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
@ConditionalOnProperty(name = "cight.ai.enabled", havingValue = "true")
public class GeminiAnalysisEngine implements AiAnalysisEngine {

    private static final String SYSTEM_PROMPT = """
            You diagnose CI/CD build failures. Be concise and evidence-driven.
            Never claim certainty unsupported by the supplied log or tool results.
            Return only JSON with keys summary, likelyRootCause, suggestedActions, confidence.
            suggestedActions must be an array of at most five concrete strings.
            confidence must be LOW, MEDIUM, or HIGH.
            """;

    private final ChatClient chatClient;
    private final AgentTools agentTools;
    private final SensitiveTextSanitizer sanitizer;
    private final ObjectMapper objectMapper;
    private final int maxLogCharacters;

    public GeminiAnalysisEngine(
            ChatClient.Builder builder,
            AgentTools agentTools,
            SensitiveTextSanitizer sanitizer,
            ObjectMapper objectMapper,
            @Value("${cight.ai.max-log-characters:16000}") int maxLogCharacters
    ) {
        this.chatClient = builder.build();
        this.agentTools = agentTools;
        this.sanitizer = sanitizer;
        this.objectMapper = objectMapper;
        this.maxLogCharacters = maxLogCharacters;
    }

    @Override
    public AnalysisResult analyze(BuildEvent build, AnalysisMode mode) {
        agentTools.beginTrace();
        try {
            String userPrompt = """
                    Repository: %s
                    Branch: %s
                    Commit: %s
                    Workflow: %s

                    Redacted failure log:
                    %s
                    """.formatted(
                    build.getRepoName(),
                    build.getBranch(),
                    build.getCommitSha(),
                    build.getWorkflowName(),
                    sanitizer.sanitize(build.getErrorLog(), maxLogCharacters)
            );

            ChatClient.ChatClientRequestSpec request = chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(userPrompt);
            if (mode == AnalysisMode.AGENTIC) {
                request = request.tools(agentTools);
            }
            String content = request.call().content();
            List<String> toolsUsed = agentTools.endTrace();
            return parse(content, toolsUsed);
        } catch (RuntimeException exception) {
            agentTools.endTrace();
            throw exception;
        }
    }

    private AnalysisResult parse(String content, List<String> toolsUsed) {
        String json = content == null ? "{}" : content.trim()
                .replaceFirst("^```(?:json)?\\s*", "")
                .replaceFirst("\\s*```$", "");
        try {
            ModelOutput output = objectMapper.readValue(json, ModelOutput.class);
            return new AnalysisResult(
                    output.summary(),
                    output.likelyRootCause(),
                    output.suggestedActions() == null
                            ? List.of()
                            : output.suggestedActions().stream().limit(5).toList(),
                    output.confidence() == null ? AnalysisConfidence.LOW : output.confidence(),
                    toolsUsed
            );
        } catch (JacksonException exception) {
            throw new IllegalStateException("Gemini returned an invalid structured response.", exception);
        }
    }

    private record ModelOutput(
            String summary,
            String likelyRootCause,
            List<String> suggestedActions,
            AnalysisConfidence confidence
    ) {
    }
}
