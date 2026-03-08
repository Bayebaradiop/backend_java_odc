package com.medibook.cabinet.entity;

import com.medibook.common.enums.JourSemaine;
import com.medibook.user.entity.Utilisateur;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Table(name = "template_semaine")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TemplateSemaine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id", nullable = false)
    private Utilisateur medecin;

    @Enumerated(EnumType.STRING)
    @Column(name = "jour_semaine", nullable = false)
    private JourSemaine jourSemaine;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    @Column(name = "duree_creneau", nullable = false)
    private Integer dureeCreneau; // minutes
}