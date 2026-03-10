package com.medibook.rendezvous.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medibook.common.enums.StatutRdv;
import com.medibook.rendezvous.entity.RendezVous;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {

    // RDV d'un patient (triés par date décroissante)
    List<RendezVous> findByPatientIdOrderByIdDesc(Long patientId);

    // RDV d'un patient par statut
    List<RendezVous> findByPatientIdAndStatut(Long patientId, StatutRdv statut);

    // RDV d'un patient avec plusieurs statuts (en attente + confirmés = à venir)
    List<RendezVous> findByPatientIdAndStatutIn(Long patientId, List<StatutRdv> statuts);

    // Historique (terminés + annulés)
    List<RendezVous> findByPatientIdAndStatutInOrderByIdDesc(Long patientId, List<StatutRdv> statuts);
}
