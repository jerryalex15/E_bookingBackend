package com.kode_project.ebooking.dto;

public record PrestataireResponseDto(
        Long prestataireId,
        UserSummaryDto user,
        String specialite,
        String adresse
) { }
