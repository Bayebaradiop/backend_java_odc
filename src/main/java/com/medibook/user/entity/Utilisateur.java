package com.medibook.user.entity;

import com.medibook.cabinet.entity.TemplateSemaine;
import com.medibook.common.enums.Role;
import com.medibook.common.enums.Status;


import com.medibook.cabinet.entity.Cabinet;
import com.medibook.rendezvous.entity.RendezVous;
import com.medibook.specialite.entity.Specialite;
import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "utilisateur")
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
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIF;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cabinet_id")
    private Cabinet cabinet; // null pour patient

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