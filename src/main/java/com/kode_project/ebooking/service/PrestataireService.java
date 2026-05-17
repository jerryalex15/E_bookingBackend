package com.kode_project.ebooking.service;

import com.kode_project.ebooking.dto.PrestataireRequestDto;
import com.kode_project.ebooking.dto.PrestataireResponseDto;
import com.kode_project.ebooking.entity.Prestataire;

import java.util.List;

public interface PrestataireService {
    List<PrestataireResponseDto> getAllPrestataire();

    PrestataireResponseDto getPrestataireById(Long id);

    PrestataireResponseDto addPrestataire(PrestataireRequestDto prestataireRequestDto);

    PrestataireResponseDto updatePrestataire(PrestataireRequestDto prestataireRequestDto, Long id);

    void deletePrestataire(Long id);
}
