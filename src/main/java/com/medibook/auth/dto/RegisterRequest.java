package com.medibook.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Requête d'inscription d'un nouveau patient")
public class RegisterRequest {
    
    @Schema(description = "Prénom de l'utilisateur", example = "Jean", required = true)
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
    private String prenom;
    
    @Schema(description = "Nom de l'utilisateur", example = "Dupont", required = true)
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;
    
    @Schema(description = "Adresse email", example = "jean.dupont@email.com", required = true)
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'adresse email est invalide")
    private String email;
    
    @Schema(description = "Numéro de téléphone", example = "+221771234567", required = true)
    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^[+]?[0-9][0-9\\s\\-()]{7,19}$", message = "Le numéro de téléphone est invalide")
    private String telephone;
    
    @Schema(description = "Mot de passe", example = "123456", required = true)
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;
}
