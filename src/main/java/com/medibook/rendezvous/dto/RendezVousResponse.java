package com.medibook.rendezvous.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.medibook.common.enums.StatutRdv;

import lombok.Builder;

@Builder
public record RendezVousResponse(
    Long id,
    StatutRdv statut,
    String motif,
    // Infos créneau
    LocalDate date,
    LocalTime heureDebut,
    LocalTime heureFin,
    // Infos médecin
    Long medecinId,
    String medecinNom,
    String medecinPrenom,
    String medecinSpecialite,
    String medecinPhoto,
    // Infos cabinet
    Long cabinetId,
    String cabinetNom,
    String cabinetAdresse
) {}
