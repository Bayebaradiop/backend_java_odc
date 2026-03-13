package com.medibook.user.dto;

import lombok.Builder;

@Builder
public record ProfileResponse(
    Long id,
    String prenom,
    String nom,
    String email,
    String telephone,
    String photo,
    String role
) {}
