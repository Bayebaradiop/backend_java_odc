package com.medibook.planning.dto;

import com.medibook.common.enums.JourSemaine;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


/**
 * DTO pour la création d'un template de planning hebdomadaire
 */
@Schema(description = "Requête pour créer un template de planning hebdomadaire")
public record PlanningRequest(

        @Schema(description = "ID du médecin", example = "1")
        @NotNull(message = "Le médecin est obligatoire")
        Long medecinId,

        @Schema(description = "Jour de la semaine", example = "LUNDI")
        @NotNull(message = "Le jour de la semaine est obligatoire")
        JourSemaine jourSemaine,

        @Schema(description = "Heure de début", example = "09:00")
        @NotBlank(message = "L'heure de début est obligatoire")
        String heureDebut,

        @Schema(description = "Heure de fin", example = "17:00")
        @NotBlank(message = "L'heure de fin est obligatoire")
        String heureFin,

        @Schema(description = "Durée d'un créneau en minutes", example = "30")
        @NotNull(message = "La durée du créneau est obligatoire")
        Integer dureeCreneau
) {}
