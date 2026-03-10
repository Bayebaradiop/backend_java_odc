package com.medibook.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Requête d'inscription d'un nouveau patient")
public class RegisterRequest {
    
    @Schema(description = "Prénom de l'utilisateur", example = "Jean", required = true)
    private String prenom;
    
    @Schema(description = "Nom de l'utilisateur", example = "Dupont", required = true)
    private String nom;
    
    @Schema(description = "Adresse email", example = "jean.dupont@email.com", required = true)
    private String email;
    
    @Schema(description = "Numéro de téléphone", example = "+221771234567", required = true)
    private String telephone;
    
    @Schema(description = "Mot de passe", example = "123456", required = true)
    private String motDePasse;
}
