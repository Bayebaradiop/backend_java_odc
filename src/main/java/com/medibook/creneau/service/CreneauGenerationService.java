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

        while (isTimeSlotValid(currentTime, template)) {
            LocalTime heureFin = currentTime.plusMinutes(template.getDureeCreneau());

            Creneau creneau = Creneau.builder()
                    .medecin(medecin)
                    .date(date)
                    .heureDebut(currentTime)
                    .heureFin(heureFin)
                    .disponible(true)
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

    /**
     * Vérifie si le créneau est valide (dans les heures d'ouverture)
     */
    private boolean isTimeSlotValid(LocalTime currentTime, TemplateSemaine template) {
        return currentTime.plusMinutes(template.getDureeCreneau()).isBefore(template.getHeureFin()) ||
               currentTime.plusMinutes(template.getDureeCreneau()).equals(template.getHeureFin());
    }
}
