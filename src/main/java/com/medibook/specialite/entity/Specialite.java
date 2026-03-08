package com.medibook.specialite.entity;

import com.medibook.user.entity.Utilisateur;
import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "specialites")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Specialite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    private String description;

    @OneToMany(mappedBy = "specialite", fetch = FetchType.LAZY)
    private Set<Utilisateur> medecins;
}