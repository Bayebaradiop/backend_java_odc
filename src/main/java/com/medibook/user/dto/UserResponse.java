package com.medibook.user.dto;

import com.medibook.common.enums.Role;
import com.medibook.common.enums.Status;

import lombok.Builder;

@Builder
public record UserResponse(
    Long id,
    String prenom,
    String nom,
    String email,
    String telephone,
    String photo,
    Role role,
    Status status,
    Long cabinetId,
    String cabinetNom,
    Long specialiteId,
    String specialiteNom
) {}
