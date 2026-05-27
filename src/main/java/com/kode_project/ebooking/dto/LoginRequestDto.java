package com.kode_project.ebooking.dto;

import lombok.Builder;

@Builder
public record LoginRequestDto(
        String email,
        String motDePasse
) {
}
