package com.medibook.rendezvous.service;

import java.util.List;
import java.time.LocalTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medibook.ExceptionsPlanning.entity.ExceptionsPlanning;
import com.medibook.ExceptionsPlanning.repository.ExceptionsPlanningRepository;
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
    private final ExceptionsPlanningRepository exceptionsPlanningRepository;
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

        // Bloque la prise de RDV si le médecin est indisponible sur ce créneau.
        if (hasBlockingException(creneau)) {
            throw new IllegalStateException(MessageErreur.MEDECIN_INDISPONIBLE_CRENEAU);
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
            throw new IllegalStateException(MessageErreur.ANNULATION_NON_AUTORISEE);
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

    // ===== Méthodes Médecin =====

    @Transactional(readOnly = true)
    public List<RendezVousResponse> getRdvMedecin() {
        Utilisateur medecin = securityService.getUtilisateurConnecte();
        return rendezVousRepository.findByMedecinIdOrderByIdDesc(medecin.getId())
                .stream().map(mapper::toRendezVousResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<RendezVousResponse> getRdvMedecinEnAttente() {
        Utilisateur medecin = securityService.getUtilisateurConnecte();
        return rendezVousRepository.findByMedecinIdAndStatut(medecin.getId(), StatutRdv.EN_ATTENTE)
                .stream().map(mapper::toRendezVousResponse).toList();
    }

    public RendezVousResponse confirmerRdv(Long rdvId) {
        Utilisateur medecin = securityService.getUtilisateurConnecte();
        RendezVous rdv = rendezVousRepository.findById(rdvId)
                .orElseThrow(() -> new EntityNotFoundException(MessageErreur.RDV_NOT_FOUND));
        if (!rdv.getMedecin().getId().equals(medecin.getId())) {
            throw new IllegalStateException(MessageErreur.RDV_NON_ASSIGNE_MEDECIN);
        }
        if (rdv.getStatut() != StatutRdv.EN_ATTENTE) {
            throw new IllegalStateException(MessageErreur.CONFIRMATION_IMPOSSIBLE);
        }
        rdv.setStatut(StatutRdv.CONFIRME);
        return mapper.toRendezVousResponse(rendezVousRepository.save(rdv));
    }

    public RendezVousResponse terminerRdv(Long rdvId) {
        Utilisateur medecin = securityService.getUtilisateurConnecte();
        RendezVous rdv = rendezVousRepository.findById(rdvId)
                .orElseThrow(() -> new EntityNotFoundException(MessageErreur.RDV_NOT_FOUND));
        if (!rdv.getMedecin().getId().equals(medecin.getId())) {
            throw new IllegalStateException(MessageErreur.RDV_NON_ASSIGNE_MEDECIN);
        }
        if (rdv.getStatut() != StatutRdv.CONFIRME) {
            throw new IllegalStateException(MessageErreur.TERMINAISON_IMPOSSIBLE);
        }
        rdv.setStatut(StatutRdv.TERMINE);
        return mapper.toRendezVousResponse(rendezVousRepository.save(rdv));
    }

    // ===== Méthodes Secrétaire =====

    @Transactional(readOnly = true)
    public List<RendezVousResponse> getRdvCabinet(Long cabinetId) {
        return rendezVousRepository.findByCabinetIdOrderByIdDesc(cabinetId)
                .stream().map(mapper::toRendezVousResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<RendezVousResponse> getRdvCabinetParStatut(Long cabinetId, StatutRdv statut) {
        return rendezVousRepository.findByCabinetIdAndStatut(cabinetId, statut)
                .stream().map(mapper::toRendezVousResponse).toList();
    }

    public RendezVousResponse confirmerRdvParSecretaire(Long rdvId, Long cabinetId) {
        RendezVous rdv = rendezVousRepository.findById(rdvId)
                .orElseThrow(() -> new EntityNotFoundException(MessageErreur.RDV_NOT_FOUND));
        if (!rdv.getCabinet().getId().equals(cabinetId)) {
            throw new IllegalStateException(MessageErreur.RDV_NON_ASSIGNE_CABINET);
        }
        if (rdv.getStatut() != StatutRdv.EN_ATTENTE) {
            throw new IllegalStateException(MessageErreur.CONFIRMATION_IMPOSSIBLE);
        }
        rdv.setStatut(StatutRdv.CONFIRME);
        return mapper.toRendezVousResponse(rendezVousRepository.save(rdv));
    }

    public RendezVousResponse annulerRdvParSecretaire(Long rdvId, Long cabinetId) {
        RendezVous rdv = rendezVousRepository.findById(rdvId)
                .orElseThrow(() -> new EntityNotFoundException(MessageErreur.RDV_NOT_FOUND));
        if (!rdv.getCabinet().getId().equals(cabinetId)) {
            throw new IllegalStateException(MessageErreur.RDV_NON_ASSIGNE_CABINET);
        }
        if (rdv.getStatut() == StatutRdv.ANNULE) {
            throw new IllegalStateException(MessageErreur.RDV_DEJA_ANNULE);
        }
        rdv.setStatut(StatutRdv.ANNULE);
        if (rdv.getCreneau() != null) {
            rdv.getCreneau().setDisponible(true);
            creneauRepository.save(rdv.getCreneau());
        }
        return mapper.toRendezVousResponse(rendezVousRepository.save(rdv));
    }

    private boolean hasBlockingException(Creneau creneau) {
        List<ExceptionsPlanning> exceptions = exceptionsPlanningRepository
                .findByMedecinIdAndDate(creneau.getMedecin().getId(), creneau.getDate());

        for (ExceptionsPlanning exception : exceptions) {
            if (exception.getHeureDebut() == null || exception.getHeureFin() == null) {
                return true;
            }

            LocalTime creneauDebut = creneau.getHeureDebut();
            LocalTime creneauFin = creneau.getHeureFin();
            LocalTime exceptionDebut = exception.getHeureDebut();
            LocalTime exceptionFin = exception.getHeureFin();

            if (creneauDebut.isBefore(exceptionFin) && creneauFin.isAfter(exceptionDebut)) {
                return true;
            }
        }

        return false;
    }
}
