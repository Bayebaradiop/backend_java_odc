package com.medibook.creneau.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Builder;

@Builder
public record CreneauResponse(
    Long id,
    LocalDate date,
    LocalTime heureDebut,
    LocalTime heureFin,
    Boolean disponible,
    Long medecinId,
    String medecinNom,
    String medecinPrenom
) {}
