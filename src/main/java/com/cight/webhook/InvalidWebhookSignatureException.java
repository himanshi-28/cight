package com.cight.webhook;

public class InvalidWebhookSignatureException extends RuntimeException {
    public InvalidWebhookSignatureException() {
        super("GitHub webhook signature is missing or invalid.");
    }
}
