package com.medibook.ExceptionsPlanning.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.medibook.common.enums.TypeException;
import com.medibook.user.entity.Utilisateur;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "exceptions_planning")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ExceptionsPlanning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id", nullable = false)
    private Utilisateur medecin;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "typeException")
    private TypeException type;

    @Column(name = "heure_debut")
    private LocalTime heureDebut;

    @Column(name = "heure_fin")
    private LocalTime heureFin;

    @Column(length = 500)
    private String motif;
}
