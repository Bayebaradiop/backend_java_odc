package com.medibook.user.dto;

public record UserRequest(
    String prenom,
    String nom,
    String telephone
) {}
