package com.kode_project.ebooking.service;

import com.kode_project.ebooking.dto.DisponibiliteRequestDto;
import com.kode_project.ebooking.dto.DisponibiliteResponseDto;

import java.util.List;

public interface DisponibiliteService {
    List<DisponibiliteResponseDto> getPrestataireDisponibility(Long prestataireId);

    DisponibiliteResponseDto addDisponibility(DisponibiliteRequestDto disponibiliteRequestDto);

    DisponibiliteResponseDto updateDisponibility(DisponibiliteRequestDto disponibiliteRequestDto, Long id);

    void deleteDisponibility(Long id);
}
