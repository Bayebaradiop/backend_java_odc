package com.medibook.creneau.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreneauRequest {
    @NotNull
    private Long medecinId;
    @NotNull
    private LocalDate date;
    @NotNull
    private LocalTime heureDebut;
    @NotNull
    private LocalTime heureFin;
}
