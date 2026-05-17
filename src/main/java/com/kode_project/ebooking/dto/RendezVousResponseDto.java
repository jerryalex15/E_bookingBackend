package com.kode_project.ebooking.dto;

import com.kode_project.ebooking.enums.Statut;

import java.time.LocalDate;
import java.time.LocalTime;

public record RendezVousResponseDto(
        Long rendezVousId,
        UserSummaryDto user,
        PrestataireSummaryDto prestataire,
        LocalDate date,
        LocalTime heureDebut,
        LocalTime heureFin,
        Statut statut
) { }
