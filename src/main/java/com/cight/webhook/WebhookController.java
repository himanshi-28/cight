package com.cight.webhook;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@RestController
@RequestMapping("/webhook/github")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookSignatureVerifier signatureVerifier;
    private final WebhookService webhookService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<WebhookResult> receive(
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            @RequestHeader("X-GitHub-Delivery") String deliveryId,
            @RequestHeader("X-GitHub-Event") String eventType,
            @RequestBody byte[] rawBody
    ) throws IOException {
        if (!signatureVerifier.isValid(rawBody, signature)) {
            throw new InvalidWebhookSignatureException();
        }
        GitHubWorkflowRunPayload payload = objectMapper.readValue(
                rawBody,
                GitHubWorkflowRunPayload.class
        );
        WebhookResult result = webhookService.process(eventType, deliveryId, payload);
        HttpStatus status = "accepted".equals(result.outcome())
                ? HttpStatus.ACCEPTED
                : HttpStatus.OK;
        return ResponseEntity.status(status).body(result);
    }
}
