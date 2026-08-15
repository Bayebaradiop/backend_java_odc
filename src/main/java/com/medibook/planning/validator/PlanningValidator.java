package com.medibook.planning.validator;

import com.medibook.common.exception.BusinessException;
import com.medibook.planning.entity.TemplateSemaine;
import com.medibook.planning.message.MessageErreur;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

@Component
public class PlanningValidator {

    /**
     * Valide qu'un nouveau créneau horaire ne chevauche aucun créneau existant pour la même journée
     */
    public void validateNonOverlapping(List<TemplateSemaine> existingTemplates, LocalTime newDebut, LocalTime newFin) {
        if (!newFin.isAfter(newDebut)) {
            throw new BusinessException(MessageErreur.HEURE_INVALIDE);
        }

        for (TemplateSemaine template : existingTemplates) {
            LocalTime existDebut = template.getHeureDebut();
            LocalTime existFin = template.getHeureFin();

            // Chevauchement si (start1 < end2) ET (end1 > start2)
            if (newDebut.isBefore(existFin) && newFin.isAfter(existDebut)) {
                throw new BusinessException("Un planning existe déjà sur cette plage horaire pour ce jour");
            }
        }
    }
}
