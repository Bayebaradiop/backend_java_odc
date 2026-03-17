package com.medibook.common.event;

import lombok.Getter;

import java.io.Serializable;

/**
 * Événement déclenché après la création d'un planning
 */
@Getter
public class PlanningCreatedEvent implements Serializable {

    private final Long planningId;
    private final Long medecinId;

    public PlanningCreatedEvent(Long planningId, Long medecinId) {
        this.planningId = planningId;
        this.medecinId = medecinId;
    }
}
