package com.medibook.planning.service;

import com.medibook.common.event.PlanningCreatedEvent;
import com.medibook.common.exception.BusinessException;
import com.medibook.common.exception.ResourceNotFoundException;
import com.medibook.planning.dto.PlanningRequest;
import com.medibook.planning.dto.PlanningResponse;
import com.medibook.planning.entity.TemplateSemaine;
import com.medibook.planning.mapper.PlanningMapper;
import com.medibook.planning.message.MessageErreur;
import com.medibook.planning.repository.PlanningRepository;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

/**
 * Service pour la gestion des plannings
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PlanningService {

    private final PlanningRepository planningRepository;
    private final UserRepository userRepository;
    private final PlanningMapper planningMapper;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Crée un template de planning hebdomadaire pour un médecin
     * Seul un secretary de la même specialty peut créer le planning
     */
    @Transactional
    public PlanningResponse createPlanning(PlanningRequest request, Long secretaireId) {
        // 1. Valider et récupérer le secretary
        Utilisateur secretaire = validateAndGetSecretaire(secretaireId);

        // 2. Valider et récupérer le médecin
        Utilisateur medecin = validateAndGetMedecin(request.medecinId());

        // 3. Valider les règles métier
        validateBusinessRules(secretaire, medecin);

        // 4. Parser les heures
        LocalTime heureDebut = LocalTime.parse(request.heureDebut());
        LocalTime heureFin = LocalTime.parse(request.heureFin());

        // 5. Créer le template
        TemplateSemaine template = buildTemplate(medecin, request, heureDebut, heureFin);
        TemplateSemaine saved = planningRepository.save(template);

        log.info("Planning créé pour le médecin {} le {}", medecin.getEmail(), request.jourSemaine());

        // 6. Publier l'événement pour générer les créneaux de manière asynchrone
        // L'événement sera traité après le commit de la transaction
        eventPublisher.publishEvent(new PlanningCreatedEvent(saved.getId(), medecin.getId()));

        return planningMapper.toResponse(saved);
    }

    /**
     * Récupère les plannings d'un médecin
     */
    @Transactional(readOnly = true)
    public List<PlanningResponse> getPlanningsByMedecin(Long medecinId) {
        return planningRepository.findByMedecinId(medecinId).stream()
                .map(planningMapper::toResponse)
                .toList();
    }

    /**
     * Supprime un planning par son ID
     */
    @Transactional
    public void deletePlanning(Long planningId) {
        if (!planningRepository.existsById(planningId)) {
            throw new ResourceNotFoundException(MessageErreur.PLANNING_NON_TROUVE);
        }
        planningRepository.deleteById(planningId);
        log.info("Planning {} supprimé", planningId);
    }

    /**
     * Valide que l'utilisateur est un secrétaire
     */
    private Utilisateur validateAndGetSecretaire(Long secretaireId) {
        Utilisateur secretaire = userRepository.findByIdWithCabinetAndSpecialite(secretaireId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageErreur.UTILISATEUR_NON_TROUVE));

        if (secretaire.getRole() != com.medibook.common.enums.Role.SECRETAIRE) {
            throw new BusinessException(MessageErreur.ACCES_SECRETAIRE_SEUL);
        }

        return secretaire;
    }

    /**
     * Valide que le médecin existe
     */
    private Utilisateur validateAndGetMedecin(Long medecinId) {
        return userRepository.findByIdWithCabinetAndSpecialite(medecinId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageErreur.MEDECIN_NON_TROUVE));
    }

    /**
     * Valide les règles métier
     */
    private void validateBusinessRules(Utilisateur secretaire, Utilisateur medecin) {
        // Valider que le médecin est dans le même cabinet
        if (!medecin.getCabinet().getId().equals(secretaire.getCabinet().getId())) {
            throw new BusinessException(MessageErreur.MEDECIN_MEME_CABINET);
        }
    }

    /**
     * Construit l'entité TemplateSemaine
     */
    private TemplateSemaine buildTemplate(Utilisateur medecin, PlanningRequest request,
                                          LocalTime heureDebut, LocalTime heureFin) {
        return TemplateSemaine.builder()
                .medecin(medecin)
                .jourSemaine(request.jourSemaine())
                .heureDebut(heureDebut)
                .heureFin(heureFin)
                .dureeCreneau(request.dureeCreneau())
                .build();
    }
}
