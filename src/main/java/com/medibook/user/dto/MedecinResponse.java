package com.medibook.user.dto;

import lombok.Builder;

@Builder
public record MedecinResponse(
    Long id,
    String prenom,
    String nom,
    String photo,
    String telephone,
    String email,
    Long specialiteId,
    String specialiteNom,
    Long cabinetId,
    String cabinetNom
) {}
