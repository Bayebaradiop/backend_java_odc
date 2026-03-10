package com.medibook.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse d'authentification")
public class AuthResponse {
    
    @Schema(description = "ID unique de l'utilisateur", example = "1")
    private Long id;
    
    @Schema(description = "Prénom", example = "Jean")
    private String prenom;
    
    @Schema(description = "Nom", example = "Dupont")
    private String nom;
    
    @Schema(description = "Adresse email", example = "jean.dupont@medibook.com")
    private String email;
    
    @Schema(description = "Rôle de l'utilisateur", example = "MEDECIN")
    private String role;
}
