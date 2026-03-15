package com.medibook.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.medibook.common.enums.Role;
import com.medibook.common.enums.Status;

/**
 * DTO pour les réponses utilisateur
 */
@Schema(description = "Réponse d'un utilisateur")
public record UserResponse(
        
        @Schema(description = "ID de l'utilisateur", example = "1")
        Long id,
        
        @Schema(description = "Prénom", example = "Jean")
        String prenom,
        
        @Schema(description = "Nom", example = "Dupont")
        String nom,
        
        @Schema(description = "Email", example = "jean.dupont@cabinet.com")
        String email,
        
        @Schema(description = "Téléphone", example = "+221771234567")
        String telephone,
        
        @Schema(description = "URL de la photo", example = "https://cloudinary.com/...")
        String photo,
        
        @Schema(description = "Rôle", example = "MEDECIN")
        Role role,
        
        @Schema(description = "Statut", example = "ACTIF")
        Status status,
        
        @Schema(description = "ID du cabinet", example = "1")
        Long cabinetId,
        
        @Schema(description = "Nom du cabinet", example = "Cabinet Central")
        String cabinetNom,
        
        @Schema(description = "ID de la spécialité (pour médecin)", example = "1")
        Long specialiteId,
        
        @Schema(description = "Nom de la spéciale (pour médecin)", example = "Cardiologie")
        String specialiteNom
) {}
