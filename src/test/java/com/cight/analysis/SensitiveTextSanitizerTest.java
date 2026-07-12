package com.cight.analysis;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SensitiveTextSanitizerTest {

    private final SensitiveTextSanitizer sanitizer = new SensitiveTextSanitizer();

    @Test
    void redactsCommonSecrets() {
        String result = sanitizer.sanitize(
                "token=secret-value\npassword: hunter2\nghp_1234567890123456789012345",
                1000
        );

        assertThat(result)
                .doesNotContain("secret-value")
                .doesNotContain("hunter2")
                .doesNotContain("ghp_");
    }

    @Test
    void boundsModelContextWhileKeepingBothEnds() {
        String result = sanitizer.sanitize("A".repeat(5000) + "TAIL", 1000);

        assertThat(result).contains("[TRUNCATED]").endsWith("TAIL");
        assertThat(result.length()).isLessThan(1100);
    }
}
