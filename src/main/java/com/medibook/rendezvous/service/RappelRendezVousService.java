package com.medibook.rendezvous.service;

import com.medibook.common.enums.StatutRdv;
import com.medibook.common.service.BrevoMailService;
import com.medibook.rendezvous.entity.RendezVous;
import com.medibook.rendezvous.repository.RendezVousRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Job planifié pour envoyer des rappels de rendez-vous par email.
 * S'exécute chaque jour à 8h00 et envoie un rappel pour les RDV confirmés du lendemain.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RappelRendezVousService {

    private final RendezVousRepository rendezVousRepository;
    private final BrevoMailService brevoMailService;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH);
    private static final DateTimeFormatter HEURE_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Job planifié : chaque jour à 8h00, envoie les rappels pour les RDV confirmés du lendemain.
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void envoyerRappels() {
        LocalDate demain = LocalDate.now().plusDays(1);
        log.info("Démarrage du job de rappel de rendez-vous pour le {}", demain);

        List<RendezVous> rdvsDemain = rendezVousRepository.findByCreneauDateAndStatut(demain, StatutRdv.CONFIRME);

        if (rdvsDemain.isEmpty()) {
            log.info("Aucun rendez-vous confirmé pour le {}", demain);
            return;
        }

        log.info("{} rendez-vous confirmé(s) trouvé(s) pour le {}", rdvsDemain.size(), demain);

        int envoyes = 0;
        int erreurs = 0;

        for (RendezVous rdv : rdvsDemain) {
            try {
                envoyerRappelPatient(rdv);
                envoyes++;
            } catch (Exception e) {
                erreurs++;
                log.error("Échec du rappel pour le RDV {} (patient: {}): {}",
                        rdv.getId(), rdv.getPatient().getEmail(), e.getMessage());
            }
        }

        log.info("Fin du job de rappel : {} envoyé(s), {} erreur(s)", envoyes, erreurs);
    }

    private void envoyerRappelPatient(RendezVous rdv) {
        String patientEmail = rdv.getPatient().getEmail();
        String patientNom = buildNom(rdv.getPatient().getPrenom(), rdv.getPatient().getNom());
        String medecinNom = buildNom(rdv.getMedecin().getPrenom(), rdv.getMedecin().getNom());
        String dateFormatee = rdv.getCreneau().getDate().format(DATE_FORMAT);
        String heureDebut = rdv.getCreneau().getHeureDebut().format(HEURE_FORMAT);
        String heureFin = rdv.getCreneau().getHeureFin().format(HEURE_FORMAT);

        String sujet = "Rappel : votre rendez-vous demain à " + heureDebut;

        String contenu = String.format(
                "Bonjour %s,\n\n" +
                "Nous vous rappelons que vous avez un rendez-vous médical prévu demain :\n\n" +
                "Date : %s\n" +
                "Heure : %s - %s\n" +
                "Médecin : Dr %s\n" +
                "%s\n\n" +
                "Si vous ne pouvez pas vous présenter, merci d'annuler votre rendez-vous depuis l'application MediBook.\n\n" +
                "Cordialement,\n" +
                "L'équipe MediBook",
                patientNom,
                dateFormatee,
                heureDebut,
                heureFin,
                medecinNom,
                rdv.getMotif() != null ? "Motif : " + rdv.getMotif() : ""
        );

        brevoMailService.sendEmail(patientEmail, sujet, contenu);
        log.info("Rappel envoyé à {} pour le RDV du {} à {}", patientEmail, dateFormatee, heureDebut);
    }

    private String buildNom(String prenom, String nom) {
        if (prenom != null && !prenom.isEmpty()) {
            return prenom + " " + (nom != null ? nom : "");
        }
        return nom != null ? nom : "";
    }
}
