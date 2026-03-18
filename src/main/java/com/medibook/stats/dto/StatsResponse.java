package com.medibook.stats.dto;

import lombok.Builder;

@Builder
public record StatsResponse(
    // Stats médecin
    Long totalRdv,
    Long rdvEnAttente,
    Long rdvConfirmes,
    Long rdvTermines,
    Long rdvAnnules,
    Long totalPatients,
    // Stats admin / super admin
    Long totalMedecins,
    Long totalSecretaires,
    Long totalCabinets
) {}
