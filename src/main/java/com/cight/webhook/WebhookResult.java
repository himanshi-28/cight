package com.cight.webhook;

import java.util.UUID;

public record WebhookResult(
        String outcome,
        UUID buildEventId
) {
    static WebhookResult ignored() {
        return new WebhookResult("ignored", null);
    }

    static WebhookResult duplicate(UUID id) {
        return new WebhookResult("duplicate", id);
    }

    static WebhookResult accepted(UUID id) {
        return new WebhookResult("accepted", id);
    }
}
