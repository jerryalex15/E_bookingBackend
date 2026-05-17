package com.kode_project.ebooking.controller;

import com.kode_project.ebooking.dto.DisponibiliteRequestDto;
import com.kode_project.ebooking.dto.DisponibiliteResponseDto;
import com.kode_project.ebooking.dto.ErrorResponseDto;
import com.kode_project.ebooking.service.DisponibiliteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disponibilites")
@AllArgsConstructor
@Tag(name = "Disponibilités", description = "Gestion des disponibilités des prestataires")

public class DisponibiliteController {

    private final DisponibiliteService disponibiliteService;

    @Operation(summary = "Lister les disponibilités d'un prestataire")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Prestataire introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/prestataire/{prestataireId}")
    public ResponseEntity<List<DisponibiliteResponseDto>> getPrestataireDisponibilites(
            @PathVariable Long prestataireId) {
        return ResponseEntity.ok(disponibiliteService.getPrestataireDisponibility(prestataireId));
    }

    @Operation(summary = "Ajouter une disponibilité",
            description = "Vérifie l'absence de conflit de créneau avant d'enregistrer la disponibilité")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Disponibilité ajoutée avec succès"),
            @ApiResponse(responseCode = "400", description = "Heure de fin antérieure ou égale à l'heure de début"),
            @ApiResponse(
                    responseCode = "409",
                    description = "Créneau en conflit avec une disponibilité existante",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping
    public ResponseEntity<DisponibiliteResponseDto> addDisponibilite(
            @RequestBody DisponibiliteRequestDto disponibiliteRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(disponibiliteService.addDisponibility(disponibiliteRequestDto));
    }

    @Operation(summary = "Modifier une disponibilité",
            description = "Vérifie l'absence de conflit en excluant la disponibilité en cours de modification")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Disponibilité mise à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Heure de fin antérieure ou égale à l'heure de début"),
            @ApiResponse(responseCode = "404", description = "Disponibilité introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Créneau en conflit avec une disponibilité existante", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<DisponibiliteResponseDto> updateDisponibilite(
            @RequestBody DisponibiliteRequestDto disponibiliteRequestDto,
            @PathVariable Long id) {
        return ResponseEntity.ok(disponibiliteService.updateDisponibility(disponibiliteRequestDto, id));
    }

    @Operation(summary = "Supprimer une disponibilité")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Disponibilité supprimée avec succès"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Disponibilité introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDisponibilite(@PathVariable Long id) {
        disponibiliteService.deleteDisponibility(id);
        return ResponseEntity.noContent().build();
    }
}
