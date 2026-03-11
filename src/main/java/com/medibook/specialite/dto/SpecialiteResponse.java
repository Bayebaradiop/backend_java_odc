package com.medibook.specialite.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO pour la réponse d'une spécialité
 */
@Schema(description = "Réponse d'une spécialité")
public record SpecialiteResponse(
        @Schema(description = "ID de la spécialité", example = "1")
        Long id,
        
        @Schema(description = "Nom de la spécialité", example = "Cardiologie")
        String nom,
        
        @Schema(description = "Description de la spécialité", example = "Spécialité concernant le cœur")
        String description,
        
        @Schema(description = "ID du cabinet", example = "1")
        Long cabinetId,
        
        @Schema(description = "Nom du cabinet", example = "Cabinet Central")
        String cabinetNom
) {}
