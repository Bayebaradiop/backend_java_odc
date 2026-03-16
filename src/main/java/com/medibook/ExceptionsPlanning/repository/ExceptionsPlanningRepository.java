package com.medibook.ExceptionsPlanning.repository;

import com.medibook.ExceptionsPlanning.entity.ExceptionPlanning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExceptionsPlanningRepository extends JpaRepository<ExceptionPlanning, Long> {

}
