package com.medibook.planning.repository;

import com.medibook.planning.entity.TemplateSemaine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanningRepository extends JpaRepository<TemplateSemaine, Long> {

}
