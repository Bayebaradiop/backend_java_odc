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

    // RDV d'un médecin (triés par id décroissant)
    List<RendezVous> findByMedecinIdOrderByIdDesc(Long medecinId);

    // RDV d'un médecin par statut
    List<RendezVous> findByMedecinIdAndStatut(Long medecinId, StatutRdv statut);

    // Comptages pour stats médecin
    long countByMedecinId(Long medecinId);
    long countByMedecinIdAndStatut(Long medecinId, StatutRdv statut);

    // Comptages pour stats admin (par cabinet)
    long countByCabinetId(Long cabinetId);
    long countByCabinetIdAndStatut(Long cabinetId, StatutRdv statut);

    // Patients distincts d'un médecin
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(DISTINCT r.patient.id) FROM RendezVous r WHERE r.medecin.id = :medecinId")
    long countDistinctPatientsByMedecinId(@org.springframework.data.repository.query.Param("medecinId") Long medecinId);

    // Patients distincts d'un cabinet
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(DISTINCT r.patient.id) FROM RendezVous r WHERE r.cabinet.id = :cabinetId")
    long countDistinctPatientsByCabinetId(@org.springframework.data.repository.query.Param("cabinetId") Long cabinetId);

    // Comptage global par statut (super admin)
    long countByStatut(StatutRdv statut);

    // RDV par cabinet (pour secrétaire)
    List<RendezVous> findByCabinetIdOrderByIdDesc(Long cabinetId);
    List<RendezVous> findByCabinetIdAndStatut(Long cabinetId, StatutRdv statut);
}
