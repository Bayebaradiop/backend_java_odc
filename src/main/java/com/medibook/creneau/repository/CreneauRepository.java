package com.medibook.creneau.repository;

import com.medibook.creneau.entity.Creneau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreneauRepository extends JpaRepository<Creneau, Long> {

}
