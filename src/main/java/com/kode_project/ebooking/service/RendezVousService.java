package com.kode_project.ebooking.service;

import com.kode_project.ebooking.dto.RendezVousRequestDto;
import com.kode_project.ebooking.dto.RendezVousResponseDto;
import com.kode_project.ebooking.dto.RendezVousUpdateDto;
import com.kode_project.ebooking.entity.RendezVous;

import java.util.List;

public interface RendezVousService {
    RendezVousResponseDto takeAppointment(RendezVousRequestDto rendezVousRequestDto);

    List<RendezVousResponseDto> getAppointmentByClientId(Long id);

    List<RendezVousResponseDto> getAppointmentByPrestataireId(Long id);

    RendezVousResponseDto updateAppointmentById(Long id, RendezVousUpdateDto rendezVousUpdateDto);

    void deleteAppointmentById(Long id);
}
