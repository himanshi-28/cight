package com.cight.mcp;

import com.cight.analytics.AnalyticsResponse;
import com.cight.analytics.AnalyticsService;
import com.cight.analysis.FailureAnalysisResponse;
import com.cight.analysis.FailureAnalysisService;
import com.cight.build.BuildEventResponse;
import com.cight.build.BuildEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "spring.ai.mcp.server.enabled",
        havingValue = "true"
)
public class CightMcpTools {

    private final BuildEventService buildEventService;
    private final AnalyticsService analyticsService;
    private final FailureAnalysisService analysisService;

    @McpTool(
            name = "get_build",
            description = "Get one CI build by its UUID."
    )
    public BuildEventResponse getBuild(
            @McpToolParam(description = "CIght build UUID") String buildId
    ) {
        return BuildEventResponse.from(buildEventService.getRequired(UUID.fromString(buildId)));
    }

    @McpTool(
            name = "list_recent_failures",
            description = "List recent failed builds for a GitHub repository."
    )
    public List<BuildEventResponse> listRecentFailures(
            @McpToolParam(description = "GitHub repository in owner/name form") String repoName,
            @McpToolParam(
                    description = "Maximum number of results from 1 to 20",
                    required = false
            ) Integer limit
    ) {
        int boundedLimit = Math.clamp(limit == null ? 5 : limit, 1, 20);
        return buildEventService.findRecentFailures(repoName, boundedLimit);
    }

    @McpTool(
            name = "get_repo_analytics",
            description = "Get CI success, failure, and duration analytics for a repository."
    )
    public AnalyticsResponse getRepoAnalytics(
            @McpToolParam(description = "GitHub repository in owner/name form") String repoName
    ) {
        return analyticsService.getAnalytics(repoName);
    }

    @McpTool(
            name = "get_failure_analysis",
            description = "Get the latest completed AI failure analysis for a build."
    )
    public FailureAnalysisResponse getFailureAnalysis(
            @McpToolParam(description = "CIght build UUID") String buildId
    ) {
        return analysisService.latestCompleted(UUID.fromString(buildId));
    }
}
