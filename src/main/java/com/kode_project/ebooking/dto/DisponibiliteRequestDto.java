package com.kode_project.ebooking.dto;

import java.time.ZonedDateTime;

public record DisponibiliteRequestDto(
        Long prestataireId,
        ZonedDateTime dateHeureDebut,
        ZonedDateTime dateHeureFin
) {
}
