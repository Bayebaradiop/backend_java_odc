package com.medibook.user.mapper;

import org.springframework.stereotype.Component;

import com.medibook.user.dto.MedecinResponse;
import com.medibook.user.entity.Utilisateur;

@Component
public class UserMapper {

    /**
     * Transforme un Utilisateur (médecin) en MedecinResponse
     */
    public MedecinResponse toMedecinResponse(Utilisateur medecin) {
        if (medecin == null) {
            return null;
        }
        
        return MedecinResponse.builder()
                .id(medecin.getId())
                .prenom(medecin.getPrenom())
                .nom(medecin.getNom())
                .photo(medecin.getPhoto())
                .telephone(medecin.getTelephone())
                .email(medecin.getEmail())
                .specialiteId(medecin.getSpecialite() != null ? medecin.getSpecialite().getId() : null)
                .specialiteNom(medecin.getSpecialite() != null ? medecin.getSpecialite().getNom() : null)
                .cabinetId(medecin.getCabinet() != null ? medecin.getCabinet().getId() : null)
                .cabinetNom(medecin.getCabinet() != null ? medecin.getCabinet().getNom() : null)
                .build();
    }
}
