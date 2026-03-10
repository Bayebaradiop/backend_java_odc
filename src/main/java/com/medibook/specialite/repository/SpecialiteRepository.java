package com.medibook.specialite.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medibook.specialite.entity.Specialite;

@Repository
public interface SpecialiteRepository extends JpaRepository<Specialite, Long> {

    List<Specialite> findByCabinetId(Long cabinetId);
}
