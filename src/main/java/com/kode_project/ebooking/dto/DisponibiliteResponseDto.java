package com.kode_project.ebooking.dto;

import com.kode_project.ebooking.enums.JourSemaine;

import java.time.LocalDate;
import java.time.LocalTime;

public record DisponibiliteResponseDto(
        Long disponibiliteId,
        PrestataireSummaryDto prestataire,
        LocalDate date,
        JourSemaine jourSemaine,
        LocalTime heureDebut,
        LocalTime heureFin
) {}
