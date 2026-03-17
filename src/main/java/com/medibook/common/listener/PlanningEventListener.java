package com.medibook.common.listener;

import com.medibook.common.event.PlanningCreatedEvent;
import com.medibook.creneau.service.CreneauGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Listener pour les événements de planning
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PlanningEventListener {

    private final CreneauGenerationService creneauGenerationService;

    /**
     * Génère les créneaux après le commit de la transaction
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPlanningCreated(PlanningCreatedEvent event) {
        log.info("Événement PlanningCreatedEvent reçu pour le planning {}", event.getPlanningId());
        creneauGenerationService.generateCreneaux(event.getPlanningId(), event.getMedecinId());
    }
}
