package com.kode_project.ebooking.dto;

import lombok.Builder;

@Builder
public record RoleSummaryDto(
        Long roleId,
        String nomRole
) {}
