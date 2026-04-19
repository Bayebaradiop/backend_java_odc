package com.medibook.ExceptionsPlanning.repository;

import com.medibook.ExceptionsPlanning.entity.ExceptionsPlanning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExceptionsPlanningRepository extends JpaRepository<ExceptionsPlanning, Long> {

    List<ExceptionsPlanning> findByMedecinId(Long medecinId);

    @Query("SELECT e FROM ExceptionsPlanning e WHERE e.medecin.id = :medecinId AND e.dateDebut <= :date AND (e.dateFin >= :date OR e.dateFin IS NULL)")
    List<ExceptionsPlanning> findByMedecinIdAndDate(@Param("medecinId") Long medecinId, @Param("date") LocalDate date);

    @Query("SELECT e FROM ExceptionsPlanning e WHERE e.medecin.id = :medecinId AND e.dateDebut <= :endDate AND (e.dateFin >= :startDate OR e.dateFin IS NULL)")
    List<ExceptionsPlanning> findByMedecinIdAndDateBetween(@Param("medecinId") Long medecinId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(e) > 0 FROM ExceptionsPlanning e WHERE e.medecin.id = :medecinId AND e.dateDebut <= :date AND (e.dateFin >= :date OR e.dateFin IS NULL)")
    boolean existsByMedecinIdAndDate(@Param("medecinId") Long medecinId, @Param("date") LocalDate date);

    void deleteByMedecinId(Long medecinId);
}
