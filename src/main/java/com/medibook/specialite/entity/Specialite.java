package com.medibook.specialite.entity;

import com.medibook.cabinet.entity.Cabinet;
import com.medibook.user.entity.Utilisateur;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(
        name = "specialites",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"nom", "cabinet_id"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Specialite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String description;

    /**
     * Chaque spécialité appartient à un cabinet
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cabinet_id", nullable = false)
    private Cabinet cabinet;

    /**
     * Médecins ayant cette spécialité
     */
    @OneToMany(mappedBy = "specialite", fetch = FetchType.LAZY)
    private Set<Utilisateur> medecins;
}