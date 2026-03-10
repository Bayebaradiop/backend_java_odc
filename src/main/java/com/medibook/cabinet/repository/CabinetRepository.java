package com.medibook.cabinet.repository;

import com.medibook.cabinet.entity.Cabinet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CabinetRepository extends JpaRepository<Cabinet, Long> {

    boolean existsByNom(String nom);

    boolean existsByEmail(String email);

    boolean existsByTelephone(String telephone);

    Optional<Cabinet> findByNom(String nom);
}
