package com.kode_project.ebooking.dto;

import java.util.List;

public record ServiceResponseDto(
        Long serviceId,
        String nomService,
        String description,
        List<PrestataireSummaryDto> prestataires
) { }
