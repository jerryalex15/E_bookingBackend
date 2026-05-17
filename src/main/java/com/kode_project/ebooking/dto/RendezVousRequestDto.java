package com.kode_project.ebooking.dto;

import java.time.ZonedDateTime;

public record RendezVousRequestDto(
        Long userId,
        Long prestataireId,
        ZonedDateTime dateHeure
) {
}
