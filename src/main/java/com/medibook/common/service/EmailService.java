package com.medibook.common.service;

import com.medibook.common.event.MedecinCreatedEvent;
import com.medibook.common.event.SecretaireCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service pour l'envoi des emails
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final BrevoMailService brevoMailService;

    @Value("${app.front-url:https://app.medibook.com}")
    private String frontUrl;

    /**
     * Envoie un email de création de compte médecin
     */
    @Async
    public void sendMedecinCreationEmail(MedecinCreatedEvent event) {
        try {
            brevoMailService.sendEmail(
                    event.getMedecin().getEmail(),
                    "Bienvenue sur MediBook",
                    buildMedecinWelcomeEmail(event)
            );
            log.info("Email de création de compte envoyé à {}", event.getMedecin().getEmail());
        } catch (Exception e) {
            log.error("Échec de l'envoi de l'email à {}: {}", event.getMedecin().getEmail(), e.getMessage());
        }
    }

    /**
     * Envoie un email de création de compte secrétaire
     */
    @Async
    public void sendSecretaireCreationEmail(SecretaireCreatedEvent event) {
        try {
            brevoMailService.sendEmail(
                    event.getSecretaire().getEmail(),
                    "Bienvenue sur MediBook",
                    buildSecretaireWelcomeEmail(event)
            );
            log.info("Email de création de compte envoyé à {}", event.getSecretaire().getEmail());
        } catch (Exception e) {
            log.error("Échec de l'envoi de l'email à {}: {}", event.getSecretaire().getEmail(), e.getMessage());
        }
    }

    /**
     * Envoie un email d'annulation d'un RDV à un patient suite à une indisponibilité exception du médecin
     */
    @Async
    public void sendRendezVousAnnuleExceptionEmail(com.medibook.common.event.RendezVousAnnuleExceptionEvent event) {
        try {
            var rdv = event.getRendezVous();
            var patient = rdv.getPatient();
            var medecin = rdv.getMedecin();
            var creneau = rdv.getCreneau();

            String patientNom = buildNomComplet(patient.getPrenom(), patient.getNom());
            String medecinNom = buildNomComplet(medecin.getPrenom(), medecin.getNom());
            String dateRdv = creneau != null ? creneau.getDate().toString() : "N/A";
            String heureRdv = creneau != null ? creneau.getHeureDebut().toString() : "N/A";
            String motif = event.getMotifException() != null && !event.getMotifException().isBlank()
                    ? event.getMotifException()
                    : "Absence / Indisponibilité exceptionnelle du praticien";

            String message = String.format(
                    "Bonjour %s,\n\n" +
                    "Nous vous informons que votre rendez-vous du %s à %s avec le Dr %s a dû être annulé en raison d'une indisponibilité exceptionnelle du praticien (%s).\n\n" +
                    "Ce créneau n'est par conséquent plus disponible.\n" +
                    "Nous vous invitons chaleureusement à vous connecter sur MediBook afin de réserver un nouveau créneau qui vous convient :\n" +
                    "%s/patient/rendezvous\n\n" +
                    "Veuillez nous excuser pour ce désagrément.\n\n" +
                    "L'équipe MediBook",
                    patientNom,
                    dateRdv,
                    heureRdv,
                    medecinNom,
                    motif,
                    frontUrl
            );

            brevoMailService.sendEmail(
                    patient.getEmail(),
                    "[MediBook] Annulation de votre rendez-vous du " + dateRdv,
                    message
            );
            log.info("Email d'annulation pour exception envoyé avec succès au patient {}", patient.getEmail());
        } catch (Exception e) {
            log.error("Échec de l'envoi de l'email d'annulation d'exception à {}: {}", event.getRendezVous().getPatient().getEmail(), e.getMessage());
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
