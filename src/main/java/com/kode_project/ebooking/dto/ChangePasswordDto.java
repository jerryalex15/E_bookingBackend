package com.kode_project.ebooking.dto;

public record ChangePasswordDto(
        String ancienMotDePasse,
        String nouveauMotDePasse
) {}
