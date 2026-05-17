package com.kode_project.ebooking.controller;


import com.kode_project.ebooking.dto.ErrorResponseDto;
import com.kode_project.ebooking.dto.PrestataireRequestDto;
import com.kode_project.ebooking.dto.PrestataireResponseDto;
import com.kode_project.ebooking.service.PrestataireService;
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
@RequestMapping("/api/prestataires")
@AllArgsConstructor
@Tag(name = "Prestataires", description = "Gestion des prestataires")
public class PrestataireController {

    private final PrestataireService prestataireService;

    @Operation(summary = "Lister tous les prestataires")
    @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    @GetMapping
    public ResponseEntity<List<PrestataireResponseDto>> getAllPrestataires() {
        return ResponseEntity.ok(prestataireService.getAllPrestataire());
    }

    @Operation(summary = "Récupérer un prestataire par son id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prestataire trouvé"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Prestataire introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<PrestataireResponseDto> getPrestataireById(@PathVariable Long id) {
        return ResponseEntity.ok(prestataireService.getPrestataireById(id));
    }

    @Operation(summary = "Ajouter un prestataire")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Prestataire créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public ResponseEntity<PrestataireResponseDto> addPrestataire(
            @RequestBody PrestataireRequestDto prestataireRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(prestataireService.addPrestataire(prestataireRequestDto));
    }

    @Operation(summary = "Mettre à jour un prestataire")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prestataire mis à jour avec succès"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Prestataire introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<PrestataireResponseDto> updatePrestataire(
            @RequestBody PrestataireRequestDto prestataireRequestDto,
            @PathVariable Long id) {
        return ResponseEntity.ok(prestataireService.updatePrestataire(prestataireRequestDto, id));
    }

    @Operation(summary = "Supprimer un prestataire")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Prestataire supprimé avec succès"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Prestataire introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrestataire(@PathVariable Long id) {
        prestataireService.deletePrestataire(id);
        return ResponseEntity.noContent().build();
    }
}
