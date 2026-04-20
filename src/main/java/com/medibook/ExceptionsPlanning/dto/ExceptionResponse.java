package com.medibook.ExceptionsPlanning.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.medibook.common.enums.TypeException;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * DTO de réponse pour une exception de planning
 */
@Schema(description = "Réponse représentant une exception de planning")
public record ExceptionResponse(

        @Schema(description = "ID de l'exception", example = "1")
        Long id,

        @Schema(description = "ID du médecin", example = "1")
        Long medecinId,

        @Schema(description = "Nom complet du médecin", example = "Dr. Dupont Jean")
        String medecinNom,

        @Schema(description = "Date de début de l'exception", example = "2025-01-20")
        LocalDate dateDebut,

        @Schema(description = "Date de fin de l'exception", example = "2025-01-22")
        LocalDate dateFin,

        @Schema(description = "Type d'exception", example = "ABSENT")
        TypeException type,

        @Schema(description = "Heure de début", example = "09:00")
        String heureDebut,

        @Schema(description = "Heure de fin", example = "12:00")
        String heureFin,

        @Schema(description = "Motif de l'exception", example = "Congé annuel")
        String motif
) {
    @JsonProperty("date")
    public LocalDate date() {
        return dateDebut;
    }
}
