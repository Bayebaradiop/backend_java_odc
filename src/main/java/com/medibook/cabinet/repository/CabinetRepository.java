package com.medibook.cabinet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medibook.cabinet.entity.Cabinet;

@Repository
public interface CabinetRepository extends JpaRepository<Cabinet, Long> {

    List<Cabinet> findByStatus(Cabinet.Status status);
    Page<Cabinet> findByStatus(Cabinet.Status status, Pageable pageable);

    boolean existsByNom(String nom);

    boolean existsByEmail(String email);

    boolean existsByTelephone(String telephone);

    Optional<Cabinet> findByNom(String nom);
}
