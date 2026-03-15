package com.medibook.user.repository;

import com.medibook.common.enums.Role;
import com.medibook.user.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByTelephone(String telephone);

    // Méthodes pour les médecins
    List<Utilisateur> findByRoleAndCabinetId(Role role, Long cabinetId);

    Optional<Utilisateur> findByIdAndCabinetId(Long id, Long cabinetId);

    boolean existsByEmailAndCabinetId(String email, Long cabinetId);

    boolean existsByTelephoneAndCabinetId(String telephone, Long cabinetId);
}
