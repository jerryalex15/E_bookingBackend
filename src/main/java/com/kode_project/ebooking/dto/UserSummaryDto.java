package com.kode_project.ebooking.dto;

public record UserSummaryDto(
        Long userId,
        String nom,
        String email
) {
}
