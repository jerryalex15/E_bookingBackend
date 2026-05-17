package com.kode_project.ebooking.controller;

import com.kode_project.ebooking.dto.StatistiquesResponseDto;
import com.kode_project.ebooking.service.StatistiquesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistiques")
@AllArgsConstructor
@Tag(name = "Statistiques", description = "Consultation des statistiques de l'application")
public class StatistiquesController {

    private final StatistiquesService statistiquesService;

    @Operation(summary = "Consulter les statistiques globales",
            description = "Retourne le nombre total de rendez-vous, le taux d'occupation, " +
                    "le nombre d'utilisateurs actifs, de prestataires et de services")
    @ApiResponse(responseCode = "200", description = "Statistiques retournées avec succès")
    @GetMapping
    public ResponseEntity<StatistiquesResponseDto> getStatistiques() {
        return ResponseEntity.ok(statistiquesService.getStatistiques());
    }
}
