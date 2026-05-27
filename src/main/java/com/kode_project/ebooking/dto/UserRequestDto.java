package com.kode_project.ebooking.dto;

public record UserRequestDto(
        String prenom,
        String nom,
        String email,
        String motDePasse
) { }
