package com.medibook.creneau.repository;

import com.medibook.creneau.entity.Creneau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

}
