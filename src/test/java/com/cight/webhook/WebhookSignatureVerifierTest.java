package com.cight.webhook;

import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;

class WebhookSignatureVerifierTest {

    @Test
    void acceptsMatchingSignatureAndRejectsTampering() throws Exception {
        String secret = "test-secret";
        byte[] body = "{\"action\":\"completed\"}".getBytes(StandardCharsets.UTF_8);
        WebhookSignatureVerifier verifier = new WebhookSignatureVerifier(secret);
        String signature = "sha256=" + sign(secret, body);

        assertThat(verifier.isValid(body, signature)).isTrue();
        assertThat(verifier.isValid("tampered".getBytes(StandardCharsets.UTF_8), signature)).isFalse();
        assertThat(verifier.isValid(body, null)).isFalse();
    }

    private String sign(String secret, byte[] body) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return HexFormat.of().formatHex(mac.doFinal(body));
    }
}
