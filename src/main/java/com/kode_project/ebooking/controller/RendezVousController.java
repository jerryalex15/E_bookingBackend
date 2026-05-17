package com.kode_project.ebooking.controller;

import com.kode_project.ebooking.dto.ErrorResponseDto;
import com.kode_project.ebooking.dto.RendezVousRequestDto;
import com.kode_project.ebooking.dto.RendezVousResponseDto;
import com.kode_project.ebooking.dto.RendezVousUpdateDto;
import com.kode_project.ebooking.service.RendezVousService;
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
@RequestMapping("/api/rendez-vous")
@AllArgsConstructor
@Tag(name = "Rendez-vous", description = "Gestion des rendez-vous entre clients et prestataires")
public class RendezVousController {

    private final RendezVousService rendezVousService;

    @Operation(summary = "Lister les rendez-vous d'un client")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès"),
            @ApiResponse(responseCode = "404", description = "Client introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/client/{id}")
    public ResponseEntity<List<RendezVousResponseDto>> getAppointmentByClientId(@PathVariable Long id) {
        return ResponseEntity.ok(rendezVousService.getAppointmentByClientId(id));
    }

    @Operation(summary = "Lister les rendez-vous d'un prestataire")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès"),
            @ApiResponse(responseCode = "404", description = "Prestataire introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/prestataire/{id}")
    public ResponseEntity<List<RendezVousResponseDto>> getAppointmentByPrestataireId(@PathVariable Long id) {
        return ResponseEntity.ok(rendezVousService.getAppointmentByPrestataireId(id));
    }

    @Operation(summary = "Prendre un rendez-vous",
            description = "Vérifie la disponibilité du prestataire et l'absence de chevauchement avant de créer le rendez-vous")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rendez-vous créé avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucune disponibilité pour ce créneau", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Créneau déjà pris", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping
    public ResponseEntity<RendezVousResponseDto> takeAppointment(
            @RequestBody RendezVousRequestDto rendezVousRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rendezVousService.takeAppointment(rendezVousRequestDto));
    }

    @Operation(summary = "Modifier un rendez-vous",
            description = "Permet de modifier la date, le prestataire ou le statut. " +
                    "Si seul le statut est fourni, la disponibilité n'est pas revérifiée")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rendez-vous mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Rendez-vous ou disponibilité introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Créneau déjà pris", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<RendezVousResponseDto> updateAppointment(
            @PathVariable Long id,
            @RequestBody RendezVousUpdateDto rendezVousUpdateDto) {
        return ResponseEntity.ok(rendezVousService.updateAppointmentById(id, rendezVousUpdateDto));
    }

    @Operation(summary = "Supprimer un service")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Service supprimé avec succès"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Service introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        rendezVousService.deleteAppointmentById(id);
        return ResponseEntity.noContent().build();
    }
}