package com.medibook.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Requête de connexion")
public class LoginRequest {
    
    @Schema(description = "Adresse email de l'utilisateur", example = "jean.dupont@medibook.com", required = true)
    private String email;
    
    @Schema(description = "Mot de passe", example = "123456", required = true)
    private String motDePasse;
}
