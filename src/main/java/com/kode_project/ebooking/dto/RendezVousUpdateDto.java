package com.kode_project.ebooking.dto;

import com.kode_project.ebooking.enums.Statut;

import java.time.ZonedDateTime;

public record RendezVousUpdateDto(
        Long prestataireId,
        ZonedDateTime dateHeure,
        Statut statut
){
}
