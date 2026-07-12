package com.cight.webhook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class WebhookSignatureVerifier {

    private final byte[] secret;

    public WebhookSignatureVerifier(@Value("${cight.webhook.secret}") String secret) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }

    public boolean isValid(byte[] body, String suppliedSignature) {
        if (suppliedSignature == null || !suppliedSignature.startsWith("sha256=")) {
            return false;
        }
        byte[] expected = ("sha256=" + hmac(body)).getBytes(StandardCharsets.UTF_8);
        byte[] supplied = suppliedSignature.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(expected, supplied);
    }

    private String hmac(byte[] body) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(body));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("HmacSHA256 is unavailable.", exception);
        } catch (java.security.InvalidKeyException exception) {
            throw new IllegalStateException("Webhook signing key is invalid.", exception);
        }
    }
}
