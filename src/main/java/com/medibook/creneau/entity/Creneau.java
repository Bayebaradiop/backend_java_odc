package com.medibook.creneau.entity;

import com.medibook.rendezvous.entity.RendezVous;
import com.medibook.user.entity.Utilisateur;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "creneaux", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"medecin_id", "date", "heure_debut"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Creneau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id", nullable = false)
    private Utilisateur medecin;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "creneau", cascade = CascadeType.ALL)
    private RendezVous rendezVous;

    @Column(nullable = false)
    private Boolean disponible = true;
}