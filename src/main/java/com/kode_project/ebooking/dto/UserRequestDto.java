package com.kode_project.ebooking.dto;

import java.util.Set;

public record UserRequestDto(
        String prenom,
        String nom,
        String email,
        String telephone,
        String motDePasse,
        Set<Long> roleIds
) { }
