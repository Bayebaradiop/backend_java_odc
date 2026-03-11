package com.medibook.specialite.repository;

import com.medibook.specialite.entity.Specialite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialiteRepository extends JpaRepository<Specialite, Long> {

    /**
     * Trouve toutes les spécialités d'un cabinet
     */
    List<Specialite> findByCabinetId(Long cabinetId);

    /**
     * Trouve une spécialité par nom et cabinet
     */
    Optional<Specialite> findByNomAndCabinetId(String nom, Long cabinetId);

    /**
     * Vérifie si une spécialité existe par nom et cabinet
     */
    boolean existsByNomAndCabinetId(String nom, Long cabinetId);
}
