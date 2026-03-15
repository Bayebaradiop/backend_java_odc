package com.medibook.common.service;

import com.medibook.common.event.MedecinCreatedEvent;
import com.medibook.common.event.SecretaireCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service pour l'envoi des emails
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.front-url:https://app.medibook.com}")
    private String frontUrl;

    /**
     * Envoie un email de création de compte médecin
     */
    @Async
    public void sendMedecinCreationEmail(MedecinCreatedEvent event) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(event.getMedecin().getEmail());
            message.setSubject("Bienvenue sur MediBook");
            message.setText(buildMedecinWelcomeEmail(event));

            mailSender.send(message);
            log.info("Email de création de compte envoyé à {}", event.getMedecin().getEmail());
        } catch (Exception e) {
            log.error("Échec de l'envoi de l'email à {}: {}", event.getMedecin().getEmail(), e.getMessage());
            // On ne lance pas d'exception pour ne pas bloquer la création du médecin
        }
    }

    /**
     * Envoie un email de création de compte secrétaire
     */
    @Async
    public void sendSecretaireCreationEmail(SecretaireCreatedEvent event) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(event.getSecretaire().getEmail());
            message.setSubject("Bienvenue sur MediBook");
            message.setText(buildSecretaireWelcomeEmail(event));

            mailSender.send(message);
            log.info("Email de création de compte envoyé à {}", event.getSecretaire().getEmail());
        } catch (Exception e) {
            log.error("Échec de l'envoi de l'email à {}: {}", event.getSecretaire().getEmail(), e.getMessage());
        }
    }

    private String buildMedecinWelcomeEmail(MedecinCreatedEvent event) {
        String nomComplet = buildNomComplet(event.getMedecin().getPrenom(), event.getMedecin().getNom());

        return String.format(
                "Bonjour Dr %s,\n\n" +
                "Votre compte médecin a été créé sur la plateforme MediBook\n" +
                "du Cabinet %s.\n\n" +
                "Vos informations de connexion :\n\n" +
                "Email : %s\n" +
                "Mot de passe temporaire : %s\n\n" +
                "Veuillez vous connecter ici :\n" +
                "%s\n\n" +
                "Nous vous recommandons de changer votre mot de passe lors de votre première connexion.\n\n" +
                "L'équipe MediBook",
                nomComplet,
                event.getCabinetNom(),
                event.getMedecin().getEmail(),
                event.getMotDePasseTemporaire(),
                frontUrl + "/login"
        );
    }

    private String buildSecretaireWelcomeEmail(SecretaireCreatedEvent event) {
        String nomComplet = buildNomComplet(event.getSecretaire().getPrenom(), event.getSecretaire().getNom());

        return String.format(
                "Bonjour %s,\n\n" +
                "Votre compte secrétaire a été créé sur la plateforme MediBook\n" +
                "du Cabinet %s.\n\n" +
                "Vos informations de connexion :\n\n" +
                "Email : %s\n" +
                "Mot de passe temporaire : %s\n\n" +
                "Veuillez vous connecter ici :\n" +
                "%s\n\n" +
                "Nous vous recommandons de changer votre mot de passe lors de votre première connexion.\n\n" +
                "L'équipe MediBook",
                nomComplet,
                event.getCabinetNom(),
                event.getSecretaire().getEmail(),
                event.getMotDePasseTemporaire(),
                frontUrl + "/login"
        );
    }

    private String buildNomComplet(String prenom, String nom) {
        if (prenom != null && !prenom.isEmpty()) {
            return prenom + " " + (nom != null ? nom : "");
        }
        return nom != null ? nom : "";
    }
}
