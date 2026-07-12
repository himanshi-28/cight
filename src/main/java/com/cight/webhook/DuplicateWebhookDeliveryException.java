package com.cight.webhook;

public class DuplicateWebhookDeliveryException extends RuntimeException {
    public DuplicateWebhookDeliveryException(String deliveryId) {
        super("GitHub delivery has already been processed: " + deliveryId);
    }
}
