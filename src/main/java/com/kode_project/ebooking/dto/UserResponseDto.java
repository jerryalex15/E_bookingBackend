package com.kode_project.ebooking.dto;

import lombok.Builder;

import java.util.List;
import java.util.Set;

@Builder
public record UserResponseDto(
        Long userId,
        String prenom,
        String nom,
        String email,
        String telephone,
        boolean activeStatut,
        RoleSummaryDto role,
        Long prestataireId
) { }
