package com.medibook.rendezvous.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medibook.common.enums.StatutRdv;
import com.medibook.common.security.SecurityService;
import com.medibook.creneau.entity.Creneau;
import com.medibook.creneau.repository.CreneauRepository;
import com.medibook.rendezvous.dto.RendezVousRequest;
import com.medibook.rendezvous.dto.RendezVousResponse;
import com.medibook.rendezvous.entity.RendezVous;
import com.medibook.rendezvous.mapper.RendezVousMapper;
import com.medibook.rendezvous.message.MessageErreur;
import com.medibook.rendezvous.repository.RendezVousRepository;
import com.medibook.user.entity.Utilisateur;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RendezVousService {

    private final RendezVousRepository rendezVousRepository;
    private final CreneauRepository creneauRepository;
    private final SecurityService securityService;
    private final RendezVousMapper mapper;

    
     // Créer un RDV (patient connecté)
     
    public RendezVousResponse creerRendezVous(RendezVousRequest request) {
        Utilisateur patient = securityService.getUtilisateurConnecte();

        // Récupérer le créneau
        Creneau creneau = creneauRepository.findById(request.getCreneauId())
                .orElseThrow(() -> new EntityNotFoundException(MessageErreur.CRENEAU_NOT_FOUND));

        // Vérifier disponibilité
        if (!creneau.getDisponible()) {
            throw new IllegalStateException(MessageErreur.CRENEAU_NON_DISPONIBLE);
        }

        // Créer le RDV
        RendezVous rdv = RendezVous.builder()
                .patient(patient)
                .medecin(creneau.getMedecin())
                .cabinet(creneau.getMedecin().getCabinet())
                .creneau(creneau)
                .statut(StatutRdv.EN_ATTENTE)
                .motif(request.getMotif())
                .build();

        // Marquer le créneau comme non disponible
        creneau.setDisponible(false);
        creneauRepository.save(creneau);

        // Sauvegarder et retourner
        return mapper.toRendezVousResponse(rendezVousRepository.save(rdv));
    }
    

    
     //Annuler un RDV (patient connecté)

    public RendezVousResponse annulerRendezVous(Long rdvId) {
        Utilisateur patient = securityService.getUtilisateurConnecte();

        RendezVous rdv = rendezVousRepository.findById(rdvId)
                .orElseThrow(() -> new EntityNotFoundException(MessageErreur.RDV_NOT_FOUND));

        // Vérifier que c'est bien le patient qui annule son RDV
        if (!rdv.getPatient().getId().equals(patient.getId())) {
            throw new IllegalStateException(MessageErreur.ANNULATION_IMPOSSIBLE);
        }

        // Vérifier statut
        if (rdv.getStatut() == StatutRdv.ANNULE) {
            throw new IllegalStateException(MessageErreur.RDV_DEJA_ANNULE);
        }

        // Annuler le RDV
        rdv.setStatut(StatutRdv.ANNULE);

        // Libérer le créneau
        if (rdv.getCreneau() != null) {
            rdv.getCreneau().setDisponible(true);
            creneauRepository.save(rdv.getCreneau());
        }

        return mapper.toRendezVousResponse(rendezVousRepository.save(rdv));
    }


    
     //Mes RDV (patient connecté)
    
    @Transactional(readOnly = true)
    public List<RendezVousResponse> getMesRendezVous() {
        Utilisateur patient = securityService.getUtilisateurConnecte();
        return rendezVousRepository.findByPatientIdOrderByIdDesc(patient.getId())
                .stream()
                .map(mapper::toRendezVousResponse)
                .toList();
    }

    
        // Mes RDV en attente
     
    @Transactional(readOnly = true)
    public List<RendezVousResponse> getMesRendezVousEnAttente() {
        Utilisateur patient = securityService.getUtilisateurConnecte();
        return rendezVousRepository.findByPatientIdAndStatut(patient.getId(), StatutRdv.EN_ATTENTE)
                .stream()
                .map(mapper::toRendezVousResponse)
                .toList();
    }


    
     // Mes RDV confirmés
     
    @Transactional(readOnly = true)
    public List<RendezVousResponse> getMesRendezVousConfirmes() {
        Utilisateur patient = securityService.getUtilisateurConnecte();
        return rendezVousRepository.findByPatientIdAndStatut(patient.getId(), StatutRdv.CONFIRME)
                .stream()
                .map(mapper::toRendezVousResponse)
                .toList();
    }


    
     //Historique (terminés + annulés)
     
    @Transactional(readOnly = true)
    public List<RendezVousResponse> getHistorique() {
        Utilisateur patient = securityService.getUtilisateurConnecte();
        return rendezVousRepository.findByPatientIdAndStatutInOrderByIdDesc(
                        patient.getId(),
                        List.of(StatutRdv.TERMINE, StatutRdv.ANNULE))
                .stream()
                .map(mapper::toRendezVousResponse)
                .toList();
    }


    
     // Détails d'un RDV
     
    @Transactional(readOnly = true)
    public RendezVousResponse getRendezVousById(Long id) {
        Utilisateur patient = securityService.getUtilisateurConnecte();
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageErreur.RDV_NOT_FOUND));

        // Vérifier que c'est bien le RDV du patient
        if (!rdv.getPatient().getId().equals(patient.getId())) {
            throw new EntityNotFoundException(MessageErreur.RDV_NOT_FOUND);
        }

        return mapper.toRendezVousResponse(rdv);
    }
}
