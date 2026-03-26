package com.medibook.ExceptionsPlanning.dto;

import com.medibook.common.enums.TypeException;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * DTO pour la création d'une exception de planning (indisponibilité)
 */
@Schema(description = "Requête pour créer une exception de planning")
public record ExceptionRequest(

        @Schema(description = "ID du médecin (optionnel si médecin connecté)", example = "1")
        Long medecinId,

        @Schema(description = "Date de l'exception", example = "2025-01-20")
        @NotNull(message = "La date est obligatoire")
        LocalDate date,

        @Schema(description = "Type d'exception", example = "ABSENT")
        @NotNull(message = "Le type d'exception est obligatoire")
        TypeException type,

        @Schema(description = "Heure de début (HH:mm), optionnel pour une journée entière", example = "09:00")
        String heureDebut,

        @Schema(description = "Heure de fin (HH:mm), optionnel pour une journée entière", example = "12:00")
        String heureFin,

        @Schema(description = "Motif de l'exception", example = "Congé annuel")
        String motif
) {}
