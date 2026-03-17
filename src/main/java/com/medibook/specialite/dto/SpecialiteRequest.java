package com.medibook.specialite.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO pour la création/modification d'une spécialité
 */
@Schema(description = "Requête pour créer ou modifier une spécialité")
public record SpecialiteRequest(
        
        @Schema(description = "Nom de la spécialité", example = "Cardiologie")
        @NotBlank(message = "Le nom de la spécialité est obligatoire")
        @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
        String nom,
        
        @Schema(description = "Description de la spécialité", example = "Spécialité concernant le cœur")
        @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
        String description
) {}
