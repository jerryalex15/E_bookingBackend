package com.kode_project.ebooking.controller;

import com.kode_project.ebooking.dto.ErrorResponseDto;
import com.kode_project.ebooking.dto.ServiceRequestDto;
import com.kode_project.ebooking.dto.ServiceResponseDto;
import com.kode_project.ebooking.service.ServiceManagementService;
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
@RequestMapping("/api/services")
@AllArgsConstructor
@Tag(name = "Services", description = "Gestion des services proposés par les prestataires")
public class ServiceController {

    private final ServiceManagementService serviceManagementService;

    @Operation(summary = "Lister tous les services")
    @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    @GetMapping
    public ResponseEntity<List<ServiceResponseDto>> getAllServices() {
        return ResponseEntity.ok(serviceManagementService.getAllService());
    }

    @Operation(summary = "Créer un nouveau service")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Service créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public ResponseEntity<ServiceResponseDto> createService(
            @RequestBody ServiceRequestDto serviceRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceManagementService.createService(serviceRequestDto));
    }

    @Operation(summary = "Mettre à jour un service")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service mis à jour avec succès"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Service introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponseDto> updateService(
            @RequestBody ServiceRequestDto serviceRequestDto,
            @PathVariable Long id) {
        return ResponseEntity.ok(serviceManagementService.updateService(serviceRequestDto, id));
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
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceManagementService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}