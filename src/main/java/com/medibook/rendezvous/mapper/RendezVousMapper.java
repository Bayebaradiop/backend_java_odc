package com.medibook.rendezvous.mapper;

import org.springframework.stereotype.Component;

import com.medibook.creneau.entity.Creneau;
import com.medibook.rendezvous.dto.RendezVousResponse;
import com.medibook.rendezvous.entity.RendezVous;
import com.medibook.user.entity.Utilisateur;

@Component
public class RendezVousMapper {

    /**
     * Convertit une entité RendezVous en RendezVousResponse
     */
    public RendezVousResponse toRendezVousResponse(RendezVous rdv) {
        if (rdv == null) {
            return null;
        }
        
        Creneau creneau = rdv.getCreneau();
        Utilisateur medecin = rdv.getMedecin();
        Utilisateur patient = rdv.getPatient();

        return RendezVousResponse.builder()
                .id(rdv.getId())
                .statut(rdv.getStatut())
                .motif(rdv.getMotif())
                // Infos créneau
                .date(creneau != null ? creneau.getDate() : null)
                .heureDebut(creneau != null ? creneau.getHeureDebut() : null)
                .heureFin(creneau != null ? creneau.getHeureFin() : null)
                // Infos médecin
                .medecinId(medecin != null ? medecin.getId() : null)
                .medecinNom(medecin != null ? medecin.getNom() : null)
                .medecinPrenom(medecin != null ? medecin.getPrenom() : null)
                .medecinSpecialite(medecin != null && medecin.getSpecialite() != null 
                        ? medecin.getSpecialite().getNom() : null)
                .medecinPhoto(medecin != null ? medecin.getPhoto() : null)
                // Infos patient
                .patientId(patient != null ? patient.getId() : null)
                .patientNom(patient != null ? patient.getNom() : null)
                .patientPrenom(patient != null ? patient.getPrenom() : null)
                .patientTelephone(patient != null ? patient.getTelephone() : null)
                // Infos cabinet
                .cabinetId(rdv.getCabinet() != null ? rdv.getCabinet().getId() : null)
                .cabinetNom(rdv.getCabinet() != null ? rdv.getCabinet().getNom() : null)
                .cabinetAdresse(rdv.getCabinet() != null ? rdv.getCabinet().getAdresse() : null)
                .build();
    }
}
