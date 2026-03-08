package com.medibook.cabinet.entity;

import com.medibook.user.entity.Utilisateur;
import com.medibook.specialite.entity.Specialite;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "cabinets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cabinet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String logo;

    @Column(name = "couleur_primaire")
    private String couleurPrimaire;

    @Column(name = "couleur_secondaire")
    private String couleurSecondaire;

    private String adresse;

    private String telephone;

    private String email;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIF;

    /**
     * Utilisateurs du cabinet
     */
    @OneToMany(mappedBy = "cabinet", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Utilisateur> utilisateurs;

    /**
     * Spécialités disponibles dans le cabinet
     */
    @OneToMany(mappedBy = "cabinet", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Specialite> specialites;

    public enum Status {
        ACTIF,
        INACTIF
    }
}