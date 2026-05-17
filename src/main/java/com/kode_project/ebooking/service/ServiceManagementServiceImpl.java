package com.kode_project.ebooking.service;

import com.kode_project.ebooking.dto.PrestataireSummaryDto;
import com.kode_project.ebooking.dto.ServiceRequestDto;
import com.kode_project.ebooking.dto.ServiceResponseDto;
import com.kode_project.ebooking.entity.Service;
import com.kode_project.ebooking.exception.ServiceNotFoundException;
import com.kode_project.ebooking.repository.ServiceRepository;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@AllArgsConstructor
public class ServiceManagementServiceImpl implements ServiceManagementService {

    private final ServiceRepository serviceRepository;

    @Override
    public List<ServiceResponseDto> getAllService() {
        return serviceRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ServiceResponseDto createService(ServiceRequestDto serviceRequestDto) {
        return entityToDto(serviceRepository.save(dtoToEntity(serviceRequestDto)));
    }

    @Override
    public ServiceResponseDto updateService(ServiceRequestDto serviceRequestDto, Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException("Service avec l'id : " + id + " introuvable"));
        service.setNomService(serviceRequestDto.nomService());
        service.setDescription(serviceRequestDto.description());
        return entityToDto(serviceRepository.save(service));
    }

    @Override
    public void deleteService(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new ServiceNotFoundException("Service avec l'id : " + id + " introuvable");
        }
        serviceRepository.deleteById(id);
    }

    private Service dtoToEntity(ServiceRequestDto dto) {
        return Service.builder()
                .nomService(dto.nomService())
                .description(dto.description())
                .build();
    }

    private ServiceResponseDto entityToDto(Service service) {
        List<PrestataireSummaryDto> prestataires =
                service.getPrestataires() == null
                        ? List.of()
                        : service.getPrestataires()
                        .stream()
                        .map(prestataire -> new PrestataireSummaryDto(
                                prestataire.getPrestataireId(),
                                prestataire.getSpecialite(),
                                prestataire.getAdresse()
                        ))
                        .toList();

        return new ServiceResponseDto(
                service.getServiceId(),
                service.getNomService(),
                service.getDescription(),
                prestataires
        );
    }
}
