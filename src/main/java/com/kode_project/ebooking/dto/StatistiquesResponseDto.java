package com.kode_project.ebooking.dto;

public record StatistiquesResponseDto(
        long totalRendezVous,
        long rendezVousConfirmes,
        long rendezVousAnnules,
        long rendezVousEnAttente,
        double tauxOccupation,        // rendezVousConfirmes / totalDisponibilites * 100
        long totalUtilisateurs,
        long totalPrestataires,
        long totalServices
) {}
