package com.medibook.cabinet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO pour la création d'un cabinet avec son administrateur
 */
public record CabinetCreateDTO(
        
        // ==================== Champs du Cabinet ====================
        
        @NotBlank(message = "Le nom du cabinet est obligatoire")
        @Size(max = 255, message = "Le nom ne peut pas dépasser 255 caractères")
        String nom,
        
        String logoUrl,
        
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "La couleur doit être au format hexadécimal (#RRGGBB)")
        String couleurPrimaire,
        
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "La couleur doit être au format hexadécimal (#RRGGBB)")
        String couleurSecondaire,
        
        @NotBlank(message = "L'adresse est obligatoire")
        @Size(max = 500, message = "L'adresse ne peut pas dépasser 500 caractères")
        String adresse,
        
        @NotBlank(message = "Le téléphone est obligatoire")
        @Pattern(regexp = "^[+]?[0-9][0-9\\s\\-()]{7,19}$", message = "Format de téléphone invalide. Ex: +221 33 123 45 67")
        String telephone,
        
        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Format d'email invalide")
        String email,
        
        // ==================== Champs de l'Administrateur ====================
        
        @NotBlank(message = "Le nom de l'administrateur est obligatoire")
        String adminNom,
        
        @NotBlank(message = "Le prénom de l'administrateur est obligatoire")
        String adminPrenom,
        
        @NotBlank(message = "L'email de l'administrateur est obligatoire")
        @Email(message = "Format d'email invalide pour l'administrateur")
        String adminEmail,
        
        @NotBlank(message = "Le téléphone de l'administrateur est obligatoire")
        @Pattern(regexp = "^[+]?[0-9][0-9\\s\\-()]{7,19}$", message = "Format de téléphone invalide pour l'administrateur")
        String adminTelephone,
        
        @NotBlank(message = "Le mot de passe de l'administrateur est obligatoire")
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
        String adminPassword
) {}
