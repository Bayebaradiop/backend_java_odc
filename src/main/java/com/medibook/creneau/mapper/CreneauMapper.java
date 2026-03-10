package com.medibook.creneau.mapper;

import org.springframework.stereotype.Component;

import com.medibook.creneau.dto.CreneauResponse;
import com.medibook.creneau.entity.Creneau;
import com.medibook.user.entity.Utilisateur;

@Component
public class CreneauMapper {

    /**
     * Convertit une entité Creneau en CreneauResponse
     */
    public CreneauResponse toCreneauResponse(Creneau creneau) {
        if (creneau == null) {
            return null;
        }
        
        Utilisateur medecin = creneau.getMedecin();
        
        return CreneauResponse.builder()
                .id(creneau.getId())
                .date(creneau.getDate())
                .heureDebut(creneau.getHeureDebut())
                .heureFin(creneau.getHeureFin())
                .disponible(creneau.getDisponible())
                .medecinId(medecin != null ? medecin.getId() : null)
                .medecinNom(medecin != null ? medecin.getNom() : null)
                .medecinPrenom(medecin != null ? medecin.getPrenom() : null)
                .build();
    }
}
