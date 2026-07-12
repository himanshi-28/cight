package com.cight.mcp;

import org.junit.jupiter.api.Test;
import org.springframework.ai.mcp.annotation.McpTool;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CightMcpToolsTest {

    @Test
    void exposesTheFourReadOnlyToolContracts() {
        Set<String> tools = Arrays.stream(CightMcpTools.class.getDeclaredMethods())
                .map(method -> method.getAnnotation(McpTool.class))
                .filter(annotation -> annotation != null)
                .map(McpTool::name)
                .collect(Collectors.toSet());

        assertThat(tools).containsExactlyInAnyOrder(
                "get_build",
                "list_recent_failures",
                "get_repo_analytics",
                "get_failure_analysis"
        );
    }
}
