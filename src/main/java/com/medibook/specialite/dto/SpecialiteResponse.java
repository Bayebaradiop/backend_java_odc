package com.medibook.specialite.dto;

import lombok.Builder;

@Builder
public record SpecialiteResponse(
    Long id,
    String nom,
    String description,
    Long cabinetId,
    String cabinetNom
) {}
