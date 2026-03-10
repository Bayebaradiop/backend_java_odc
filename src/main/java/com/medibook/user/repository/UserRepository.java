package com.medibook.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medibook.common.enums.Role;
import com.medibook.common.enums.Status;
import com.medibook.user.entity.Utilisateur;

@Repository
public interface UserRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByTelephone(String telephone);

    // Recherche de médecins avec filtres
    List<Utilisateur> findByRoleAndStatus(Role role, Status status);

    List<Utilisateur> findByRoleAndStatusAndSpecialiteId(Role role, Status status, Long specialiteId);

    List<Utilisateur> findByRoleAndStatusAndCabinetId(Role role, Status status, Long cabinetId);

    List<Utilisateur> findByRoleAndStatusAndSpecialiteIdAndCabinetId(
            Role role, Status status, Long specialiteId, Long cabinetId);
}
