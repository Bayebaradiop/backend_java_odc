package com.medibook.creneau.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;

import com.medibook.creneau.entity.Creneau;

@Repository
public interface CreneauRepository extends JpaRepository<Creneau, Long> {

    /**
     * Trouve la dernière date de créneau pour un médecin
     */
    @Query("SELECT MAX(c.date) FROM Creneau c WHERE c.medecin.id = :medecinId")
    Optional<LocalDate> findMaxDateByMedecinId(@Param("medecinId") Long medecinId);

    /**
     * Trouve tous les médecins qui ont au moins un créneau
     */
    @Query("SELECT DISTINCT c.medecin.id FROM Creneau c")
    List<Long> findDistinctMedecinIds();

    // Créneaux disponibles pour une date précise
    List<Creneau> findByMedecinIdAndDateAndDisponibleTrue(Long medecinId, LocalDate date);

    // Créneaux disponibles sur une période (triés par date et heure)
    List<Creneau> findByMedecinIdAndDateBetweenAndDisponibleTrueOrderByDateAscHeureDebutAsc(
            Long medecinId, LocalDate startDate, LocalDate endDate);

    // Tous les créneaux d'un médecin
    List<Creneau> findByMedecinId(Long medecinId);
}
