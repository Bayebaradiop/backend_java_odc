package com.medibook.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO pour la création/mise à jour d'un médecin
 * La photo est uploadée séparément via le paramètre multipart
 */
@Schema(description = "Requête pour créer/mettre à jour un médecin")
public record MedecinRequest(
        
        @Schema(description = "Prénom du médecin", example = "Jean")
        @NotBlank(message = "Le prénom est obligatoire")
        @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
        String prenom,
        
        @Schema(description = "Nom du médecin", example = "Dupont")
        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
        String nom,
        
        @Schema(description = "Email du médecin", example = "jean.dupont@cabinet.com")
        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'adresse email est invalide")
        String email,
        
        @Schema(description = "Téléphone du médecin", example = "+221771234567")
        @NotBlank(message = "Le téléphone est obligatoire")
        @Pattern(regexp = "^[+]?[0-9][0-9\\s\\-()]{7,19}$", message = "Le numéro de téléphone est invalide")
        String telephone,
        
        @Schema(description = "Mot de passe temporaire (optionnel pour mise à jour)", example = "MotDePasse123!")
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
        String motDePasse,
        
        @Schema(description = "ID de la spécialité", example = "1")
        @NotNull(message = "La spécialité est obligatoire")
        Long specialiteId
) {}
