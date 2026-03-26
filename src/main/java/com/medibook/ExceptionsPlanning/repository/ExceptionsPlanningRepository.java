package com.medibook.ExceptionsPlanning.repository;

import com.medibook.ExceptionsPlanning.entity.ExceptionPlanning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExceptionsPlanningRepository extends JpaRepository<ExceptionPlanning, Long> {

    List<ExceptionsPlanning> findByMedecinId(Long medecinId);

    List<ExceptionsPlanning> findByMedecinIdAndDate(Long medecinId, LocalDate date);

    boolean existsByMedecinIdAndDate(Long medecinId, LocalDate date);

    void deleteByMedecinId(Long medecinId);
}
