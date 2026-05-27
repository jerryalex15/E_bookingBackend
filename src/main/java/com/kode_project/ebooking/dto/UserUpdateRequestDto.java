package com.kode_project.ebooking.dto;

public record UserUpdateRequestDto(
        String prenom,
        String nom,
        String email,
        String telephone,
        Long roleId
) { }
