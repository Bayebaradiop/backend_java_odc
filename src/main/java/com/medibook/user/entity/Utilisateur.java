package com.medibook.user.entity;

import java.util.Set;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.medibook.cabinet.entity.Cabinet;
import com.medibook.common.enums.Role;
import com.medibook.common.enums.Status;
import com.medibook.planning.entity.TemplateSemaine;
import com.medibook.rendezvous.entity.RendezVous;
import com.medibook.specialite.entity.Specialite;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "utilisateurs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String telephone;

    @Column(nullable = false)
    private String motDePasse;

    private String photo;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "role_enum")
    private Role role;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Builder.Default
    @Column(columnDefinition = "statut_enum")
    private Status status = Status.ACTIF;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cabinet_id")
    private Cabinet cabinet; // null pour patient ou super_admin

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialite_id")
    private Specialite specialite; // null si role != medecin

    @OneToMany(mappedBy = "medecin", fetch = FetchType.LAZY)
    private Set<TemplateSemaine> plannings;

    @OneToMany(mappedBy = "medecin", fetch = FetchType.LAZY)
    private Set<RendezVous> rendezVousMedecin;

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    private Set<RendezVous> rendezVousPatient;
}