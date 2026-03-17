package com.medibook.planning.dto;

import com.medibook.common.enums.JourSemaine;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO pour la réponse d'un template de planning hebdomadaire
 */
@Schema(description = "Réponse d'un template de planning hebdomadaire")
public record PlanningResponse(

        @Schema(description = "ID du template", example = "1")
        Long id,

        @Schema(description = "ID du médecin", example = "1")
        Long medecinId,

        @Schema(description = "Nom du médecin", example = "Dr Jean Dupont")
        String medecinNom,

        @Schema(description = "Jour de la semaine", example = "LUNDI")
        JourSemaine jourSemaine,

        @Schema(description = "Heure de début", example = "09:00")
        String heureDebut,

        @Schema(description = "Heure de fin", example = "17:00")
        String heureFin,

        @Schema(description = "Durée d'un créneau en minutes", example = "30")
        Integer dureeCreneau
) {}
