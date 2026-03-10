package com.medibook.creneau.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medibook.creneau.entity.Creneau;

@Repository
public interface CreneauRepository extends JpaRepository<Creneau, Long> {

    // Créneaux disponibles pour une date précise
    List<Creneau> findByMedecinIdAndDateAndDisponibleTrue(Long medecinId, LocalDate date);

    // Créneaux disponibles sur une période (triés par date et heure)
    List<Creneau> findByMedecinIdAndDateBetweenAndDisponibleTrueOrderByDateAscHeureDebutAsc(
            Long medecinId, LocalDate startDate, LocalDate endDate);

    // Tous les créneaux d'un médecin
    List<Creneau> findByMedecinId(Long medecinId);
}
