package com.medibook.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Requête de connexion")
public class LoginRequest {
    
    @Schema(description = "Adresse email de l'utilisateur", example = "jean.dupont@medibook.com", required = true)
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'adresse email est invalide")
    private String email;
    
    @Schema(description = "Mot de passe", example = "123456", required = true)
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;
}
