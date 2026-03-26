package com.medibook.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO pour la création/mise à jour d'un/e secrétaire
 * La photo est uploadée séparément via le paramètre multipart
 */
@Schema(description = "Requête pour créer/mettre à jour un/e secrétaire")
public record SecretaireRequest(
        
        @Schema(description = "Prénom du/de la secrétaire", example = "Marie")
        @NotBlank(message = "Le prénom est obligatoire")
        @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
        String prenom,
        
        @Schema(description = "Nom du/de la secrétaire", example = "Dupont")
        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
        String nom,
        
        @Schema(description = "Email du/de la secrétaire", example = "marie.dupont@cabinet.com")
        @NotBlank(message = "L'email est obligatoire")
        String email,
        
        @Schema(description = "Téléphone du/de la secrétaire", example = "+221771234567")
        @NotBlank(message = "Le téléphone est obligatoire")
        String telephone,
        
        @Schema(description = "Mot de passe temporaire (optionnel pour mise à jour)", example = "MotDePasse123!")
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
        String motDePasse
) {}
