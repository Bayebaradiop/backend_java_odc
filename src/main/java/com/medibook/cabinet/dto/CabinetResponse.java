package com.medibook.cabinet.dto;

import lombok.Builder;

@Builder
public record CabinetResponse(
    Long id,
    String nom,
    String logo,
    String adresse,
    String telephone,
    String email,
    String couleurPrimaire,
    String couleurSecondaire
) {}
