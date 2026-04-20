package com.medibook.ExceptionsPlanning.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.medibook.common.enums.TypeException;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO pour la création d'une exception de planning (indisponibilité)
 */
@Schema(description = "Requête pour créer une exception de planning")
public record ExceptionRequest(

        @Schema(description = "ID du médecin (optionnel si médecin connecté)", example = "1")
        Long medecinId,

        @Schema(description = "Date de début de l'exception", example = "2025-01-20")
        @JsonAlias("date")
        @NotNull(message = "La date de début est obligatoire")
        LocalDate dateDebut,

        @Schema(description = "Date de fin de l'exception", example = "2025-01-22")
        LocalDate dateFin,

        @Schema(description = "Type d'exception", example = "ABSENT")
        @NotNull(message = "Le type d'exception est obligatoire")
        TypeException type,

        @Schema(description = "Heure de début (HH:mm), optionnel pour une journée entière", example = "09:00")
        @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "L'heure de début doit être au format HH:mm")
        String heureDebut,

        @Schema(description = "Heure de fin (HH:mm), optionnel pour une journée entière", example = "12:00")
        @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "L'heure de fin doit être au format HH:mm")
        String heureFin,

        @Schema(description = "Motif de l'exception", example = "Congé annuel")
        @Size(max = 500, message = "Le motif ne peut pas dépasser 500 caractères")
        String motif
) {
    public LocalDate getDateFinOrDefault() {
        return dateFin != null ? dateFin : dateDebut;
    }
}
