package com.kode_project.ebooking.dto;

public record PrestataireRequestDto(
        Long userId,
        String specialite,
        String adresse,
        Long serviceId
) {
}
