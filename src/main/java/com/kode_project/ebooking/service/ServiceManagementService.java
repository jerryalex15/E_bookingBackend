package com.kode_project.ebooking.service;

import com.kode_project.ebooking.dto.ServiceRequestDto;
import com.kode_project.ebooking.dto.ServiceResponseDto;

import java.util.List;

public interface ServiceManagementService {
    List<ServiceResponseDto> getAllService();

    ServiceResponseDto createService(ServiceRequestDto serviceRequestDto);

    ServiceResponseDto updateService(ServiceRequestDto serviceRequestDto, Long id);

    void deleteService(Long id);
}
