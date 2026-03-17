package com.medibook.common.listener;

import com.medibook.common.event.MedecinCreatedEvent;
import com.medibook.common.event.SecretaireCreatedEvent;
import com.medibook.common.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Listener pour les événements liés aux emails
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailEventListener {

    private final EmailService emailService;

    /**
     * Écoute l'événement de création d'un médecin et envoie l'email
     */
    @Async
    @EventListener
    public void handleMedecinCreated(MedecinCreatedEvent event) {
        log.info("Événement MedecinCreatedEvent reçu pour: {}", event.getMedecin().getEmail());
        emailService.sendMedecinCreationEmail(event);
    }

    /**
     * Écoute l'événement de création d'un(e) secrétaire et envoie l'email
     */
    @Async
    @EventListener
    public void handleSecretaireCreated(SecretaireCreatedEvent event) {
        log.info("Événement SecretaireCreatedEvent reçu pour: {}", event.getSecretaire().getEmail());
        emailService.sendSecretaireCreationEmail(event);
    }
}
