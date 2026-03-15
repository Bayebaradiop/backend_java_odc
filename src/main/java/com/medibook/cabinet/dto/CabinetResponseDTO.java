package com.medibook.cabinet.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * DTO pour la réponse d'un cabinet
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CabinetResponseDTO(
        // Informations du cabinet
        Long id,
        String nom,
        String logo,
        String couleurPrimaire,
        String couleurSecondaire,
        String adresse,
        String telephone,
        String email,
        String status,
        
        // Informations de l'administrateur (optionnel - null lors d'une simple consultation)
        AdminInfo admin
) {
    /**
     * DTO imbriqué pour les informations de l'administrateur
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AdminInfo(
            Long id,
            String nom,
            String prenom,
            String email,
            String telephone
    ) {}
    
    /**
     * Constructeur简便 pour créer un CabinetResponseDTO sans admin
     */
    public CabinetResponseDTO(Long id, String nom, String logo, String couleurPrimaire, 
                              String couleurSecondaire, String adresse, String telephone, 
                              String email, String status) {
        this(id, nom, logo, couleurPrimaire, couleurSecondaire, adresse, telephone, email, status, null);
    }
}
