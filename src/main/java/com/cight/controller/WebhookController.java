package com.cight.controller;

import com.cight.dto.GitHubWebhookPayload;
import com.cight.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j

public class WebhookController {
    private final WebhookService webhookService;

    @PostMapping
    public ResponseEntity<Void>receiveWebhook(
            @RequestBody GitHubWebhookPayload payload){
        log.info("Received Webhook Request");
        webhookService.processWebhook(payload);
        return ResponseEntity.ok().build();
    }
}
