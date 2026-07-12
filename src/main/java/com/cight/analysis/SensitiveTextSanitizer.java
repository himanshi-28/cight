package com.cight.analysis;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class SensitiveTextSanitizer {

    private static final List<Pattern> SECRET_PATTERNS = List.of(
            Pattern.compile("(?i)(api[_-]?key|token|password|secret)\\s*[=:]\\s*[^\\s]+"),
            Pattern.compile("gh[pousr]_[A-Za-z0-9_]{20,}"),
            Pattern.compile("AIza[0-9A-Za-z_-]{30,}"),
            Pattern.compile("(?i)authorization:\\s*bearer\\s+[^\\s]+")
    );

    public String sanitize(String input, int maxCharacters) {
        if (input == null || input.isBlank()) {
            return "No build error log was supplied.";
        }
        String sanitized = input;
        for (Pattern pattern : SECRET_PATTERNS) {
            sanitized = pattern.matcher(sanitized).replaceAll("[REDACTED]");
        }
        if (sanitized.length() <= maxCharacters) {
            return sanitized;
        }

        int headLength = Math.min(4000, maxCharacters / 4);
        int tailLength = maxCharacters - headLength;
        return sanitized.substring(0, headLength)
                + "\n...[TRUNCATED]...\n"
                + sanitized.substring(sanitized.length() - tailLength);
    }
}
