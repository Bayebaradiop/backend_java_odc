package com.medibook.creneau.service;

import com.medibook.creneau.entity.Creneau;
import com.medibook.creneau.repository.CreneauRepository;
import com.medibook.planning.entity.TemplateSemaine;
import com.medibook.planning.repository.PlanningRepository;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour la génération des créneaux
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreneauGenerationService {

    private final CreneauRepository creneauRepository;
    private final PlanningRepository planningRepository;
    private final UserRepository userRepository;
    private final com.medibook.ExceptionsPlanning.repository.ExceptionsPlanningRepository exceptionsPlanningRepository;

    // Constantes de configuration
    private static final int JOURS_GENERATION = 30;
    private static final int TAILLE_BATCH = 1000;

    /**
     * Génère les créneaux pour 30 jours de manière asynchrone
     */
    @Async
    @Transactional
    public void generateCreneaux(Long planningId, Long medecinId) {
        log.info("Démarrage de la génération asynchrone des créneaux pour le planning {}", planningId);
        
        // Recharger les entités dans cette transaction
        TemplateSemaine template = planningRepository.findById(planningId).orElse(null);
        if (template == null) {
            log.error("Planning non trouvé: {}", planningId);
            return;
        }
        
        Utilisateur medecin = userRepository.findById(medecinId).orElse(null);
        if (medecin == null) {
            log.error("Médecin non trouvé: {}", medecinId);
            return;
        }
        
        log.info("Template trouvé: jour={}, debut={}, fin={}, duree={}", 
                template.getJourSemaine(), template.getHeureDebut(), template.getHeureFin(), template.getDureeCreneau());
        
        List<Creneau> creneaux = new ArrayList<>();
        LocalDate dateDebut = LocalDate.now();
        int joursGeneres = 0;

        try {
            while (joursGeneres < JOURS_GENERATION) {
                // Trouver le prochain jour correspondant au jour de la semaine
                while (!dateDebut.getDayOfWeek().equals(template.getJourSemaine().toDayOfWeek())) {
                    dateDebut = dateDebut.plusDays(1);
                }

                log.info("Génération des créneaux pour la date: {}", dateDebut);

                // Générer les créneaux pour ce jour
                generateCreneauxForDay(template, medecin, dateDebut, creneaux);

                dateDebut = dateDebut.plusDays(7);
                joursGeneres += 7;
            }

            // Sauvegarder les créneaux restants
            if (!creneaux.isEmpty()) {
                creneauRepository.saveAll(creneaux);
            }

            log.info("Génération asynchrone terminée: {} créneaux générés pour le médecin {}", 
                    creneaux.size(), medecin.getEmail());
                    
        } catch (Exception e) {
            log.error("Erreur lors de la génération asynchrone des créneaux pour le médecin {}: {}", 
                    medecin.getEmail(), e.getMessage(), e);
            // Sauvegarder ce qui a été généré en cas d'erreur
            if (!creneaux.isEmpty()) {
                creneauRepository.saveAll(creneaux);
            }
        }
    }

    /**
     * Génère les créneaux pour une journée
     */
    private void generateCreneauxForDay(TemplateSemaine template, Utilisateur medecin,
                                        LocalDate date, List<Creneau> creneaux) {
        LocalTime currentTime = template.getHeureDebut();
        List<com.medibook.ExceptionsPlanning.entity.ExceptionsPlanning> exceptionsDay = 
                exceptionsPlanningRepository.findByMedecinIdAndDate(medecin.getId(), date);

        while (isTimeSlotValid(currentTime, template)) {
            LocalTime heureFin = currentTime.plusMinutes(template.getDureeCreneau());

            boolean blockedByException = isSlotBlockedByException(currentTime, heureFin, exceptionsDay);

            Creneau creneau = Creneau.builder()
                    .medecin(medecin)
                    .date(date)
                    .heureDebut(currentTime)
                    .heureFin(heureFin)
                    .disponible(!blockedByException)
                    .build();

            creneaux.add(creneau);

            // Sauvegarder par lots pour éviter les problèmes de mémoire
            if (creneaux.size() >= TAILLE_BATCH) {
                creneauRepository.saveAll(creneaux);
                creneaux.clear();
            }

            currentTime = heureFin;
        }
    }

    private boolean isSlotBlockedByException(LocalTime creneauDebut, LocalTime creneauFin,
                                             List<com.medibook.ExceptionsPlanning.entity.ExceptionsPlanning> exceptions) {
        for (com.medibook.ExceptionsPlanning.entity.ExceptionsPlanning ex : exceptions) {
            if (ex.getHeureDebut() == null || ex.getHeureFin() == null) {
                return true; // Exception sur la journée entière
            }
            if (creneauDebut.isBefore(ex.getHeureFin()) && creneauFin.isAfter(ex.getHeureDebut())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si le créneau est valide (dans les heures d'ouverture)
     */
    private boolean isTimeSlotValid(LocalTime currentTime, TemplateSemaine template) {
        return currentTime.plusMinutes(template.getDureeCreneau()).isBefore(template.getHeureFin()) ||
               currentTime.plusMinutes(template.getDureeCreneau()).equals(template.getHeureFin());
    }

    /**
     * Job planifié qui s'exécute chaque nuit à 2h00
     * Génère les créneaux pour maintenir 30 jours d'avance
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void regenerateCreneaux() {
        log.info("Démarrage du job de regeneration des créneaux");

        // Trouver tous les médecins avec des créneaux
        List<Long> medecinIds = creneauRepository.findDistinctMedecinIds();
        log.info("Nombre de médecins à traiter: {}", medecinIds.size());

        for (Long medecinId : medecinIds) {
            try {
                regenerateCreneauxForMedecin(medecinId);
            } catch (Exception e) {
                log.error("Erreur lors de la regeneration des créneaux pour le médecin {}: {}",
                        medecinId, e.getMessage(), e);
            }
        }

        log.info("Fin du job de regeneration des créneaux");
    }

    /**
     * Regénère les créneaux pour un médecin spécifique
     */
    private void regenerateCreneauxForMedecin(Long medecinId) {
        // Trouver la dernière date de créneau
        LocalDate lastDate = creneauRepository.findMaxDateByMedecinId(medecinId).orElse(null);

        if (lastDate == null) {
            log.debug("Aucun créneau trouvé pour le médecin {}", medecinId);
            return;
        }

        LocalDate targetDate = LocalDate.now().plusDays(JOURS_GENERATION);

        // Si on a déjà 30 jours, pas besoin de regenerer
        if (lastDate.isAfter(targetDate)) {
            log.debug("Le médecin {} a déjà des créneaux jusqu'au {}", medecinId, lastDate);
            return;
        }

        log.info("Regénération des créneaux pour le médecin {}: dernier={}, cible={}",
                medecinId, lastDate, targetDate);

        // Récupérer les plannings du médecin
        List<TemplateSemaine> plannings = planningRepository.findByMedecinId(medecinId);

        if (plannings.isEmpty()) {
            log.debug("Aucun planning trouvé pour le médecin {}", medecinId);
            return;
        }

        // Récupérer le médecin
        Utilisateur medecin = userRepository.findById(medecinId).orElse(null);
        if (medecin == null) {
            return;
        }

        // Générer les créneaux à partir de la dernière date + 1 jour
        LocalDate dateDebut = lastDate.plusDays(1);
        int joursGeneres = 0;
        List<Creneau> creneaux = new ArrayList<>();

        while (dateDebut.isBefore(targetDate) || dateDebut.isEqual(targetDate)) {
            // Trouver le planning pour ce jour de la semaine
            DayOfWeek dayOfWeek = dateDebut.getDayOfWeek();

            for (TemplateSemaine planning : plannings) {
                if (planning.getJourSemaine().toDayOfWeek().equals(dayOfWeek)) {
                    generateCreneauxForDay(planning, medecin, dateDebut, creneaux);
                }
            }

            dateDebut = dateDebut.plusDays(1);
            joursGeneres++;

            // Sauvegarder par lots
            if (creneaux.size() >= TAILLE_BATCH) {
                creneauRepository.saveAll(creneaux);
                creneaux.clear();
            }
        }

        // Sauvegarder les créneaux restants
        if (!creneaux.isEmpty()) {
            creneauRepository.saveAll(creneaux);
        }

        log.info("Regénération terminée pour le médecin {}: {} jours traités",
                medecinId, joursGeneres);
    }
}
