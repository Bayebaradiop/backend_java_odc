package com.medibook.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BrevoMailService {

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    @Value("${brevo.api-key}")
    private String apiKey;

    @Value("${brevo.sender-email}")
    private String senderEmail;

    @Value("${brevo.sender-name:MediBook}")
    private String senderName;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendEmail(String toEmail, String subject, String textContent) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        Map<String, Object> body = Map.of(
                "sender", Map.of("name", senderName, "email", senderEmail),
                "to", List.of(Map.of("email", toEmail)),
                "subject", subject,
                "textContent", textContent
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_API_URL, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email envoyé via Brevo API à {}", toEmail);
            } else {
                log.error("Brevo API a retourné le code {}: {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("Échec de l'envoi via Brevo API: " + response.getStatusCode());
            }
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("Échec")) {
                throw e;
            }
            log.error("Erreur lors de l'appel Brevo API pour {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi de l'email via Brevo API", e);
        }
    }
}
