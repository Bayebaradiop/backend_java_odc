package com.medibook.auth.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private Long id;
    private String prenom;
    private String nom;
    private String email;
    private String role;
}
