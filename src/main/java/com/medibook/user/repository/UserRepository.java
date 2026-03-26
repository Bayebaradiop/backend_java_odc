package com.medibook.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.medibook.common.enums.Role;
import com.medibook.common.enums.Status;
import com.medibook.user.entity.Utilisateur;

@Repository
public interface UserRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByTelephone(String telephone);

    // Recherche avec filtres (bara_dev - patient search)
    List<Utilisateur> findByRoleAndStatus(Role role, Status status);

    List<Utilisateur> findByRoleAndStatusAndSpecialiteId(Role role, Status status, Long specialiteId);

    List<Utilisateur> findByRoleAndStatusAndCabinetId(Role role, Status status, Long cabinetId);

    List<Utilisateur> findByRoleAndStatusAndSpecialiteIdAndCabinetId(
            Role role, Status status, Long specialiteId, Long cabinetId);

    // Méthodes admin (lydevtech)
    List<Utilisateur> findByRoleAndCabinetId(Role role, Long cabinetId);

    List<Utilisateur> findByRoleAndCabinetIdAndSpecialiteId(Role role, Long cabinetId, Long specialiteId);

    Optional<Utilisateur> findByIdAndCabinetId(Long id, Long cabinetId);

    boolean existsByEmailAndCabinetId(String email, Long cabinetId);

    boolean existsByTelephoneAndCabinetId(String telephone, Long cabinetId);

    @Query("SELECT u FROM Utilisateur u LEFT JOIN FETCH u.cabinet LEFT JOIN FETCH u.specialite WHERE u.id = :id")
    Optional<Utilisateur> findByIdWithCabinetAndSpecialite(@Param("id") Long id);

    @Query("SELECT u FROM Utilisateur u LEFT JOIN FETCH u.cabinet LEFT JOIN FETCH u.specialite WHERE u.email = :email")
    Optional<Utilisateur> findByEmailWithCabinetAndSpecialite(@Param("email") String email);

    // Comptages pour stats
    long countByRoleAndCabinetId(Role role, Long cabinetId);
    long countByRole(Role role);
}
