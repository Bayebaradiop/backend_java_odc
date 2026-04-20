package com.medibook.rendezvous.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.medibook.common.enums.StatutRdv;
import com.medibook.rendezvous.entity.RendezVous;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {

    // RDV d'un patient (triés par date décroissante)
    List<RendezVous> findByPatientIdOrderByIdDesc(Long patientId);
    Page<RendezVous> findByPatientIdOrderByIdDesc(Long patientId, Pageable pageable);

    // RDV d'un patient par statut
    List<RendezVous> findByPatientIdAndStatut(Long patientId, StatutRdv statut);
    Page<RendezVous> findByPatientIdAndStatut(Long patientId, StatutRdv statut, Pageable pageable);

    // RDV d'un patient avec plusieurs statuts (en attente + confirmés = à venir)
    List<RendezVous> findByPatientIdAndStatutIn(Long patientId, List<StatutRdv> statuts);

    // Historique (terminés + annulés)
    List<RendezVous> findByPatientIdAndStatutInOrderByIdDesc(Long patientId, List<StatutRdv> statuts);
    Page<RendezVous> findByPatientIdAndStatutInOrderByIdDesc(Long patientId, List<StatutRdv> statuts, Pageable pageable);

    // RDV d'un médecin (triés par id décroissant)
    List<RendezVous> findByMedecinIdOrderByIdDesc(Long medecinId);
    Page<RendezVous> findByMedecinIdOrderByIdDesc(Long medecinId, Pageable pageable);

    // RDV d'un médecin par statut
    List<RendezVous> findByMedecinIdAndStatut(Long medecinId, StatutRdv statut);
    Page<RendezVous> findByMedecinIdAndStatut(Long medecinId, StatutRdv statut, Pageable pageable);

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
    Page<RendezVous> findByCabinetIdOrderByIdDesc(Long cabinetId, Pageable pageable);
    List<RendezVous> findByCabinetIdAndStatut(Long cabinetId, StatutRdv statut);
    Page<RendezVous> findByCabinetIdAndStatut(Long cabinetId, StatutRdv statut, Pageable pageable);

    // RDV confirmés pour une date donnée (rappels)
    @Query("SELECT r FROM RendezVous r " +
           "JOIN FETCH r.patient " +
           "JOIN FETCH r.medecin " +
           "JOIN FETCH r.creneau c " +
           "WHERE c.date = :date AND r.statut = :statut")
    List<RendezVous> findByCreneauDateAndStatut(@Param("date") LocalDate date, @Param("statut") StatutRdv statut);
}
