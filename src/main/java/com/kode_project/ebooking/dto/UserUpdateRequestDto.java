package com.kode_project.ebooking.dto;

import java.util.Set;

public record UserUpdateRequestDto(
        String prenom,
        String nom,
        String email,
        String telephone,
        Set<Long> roleIds
) { }
