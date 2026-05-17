package com.kode_project.ebooking.service;

import com.kode_project.ebooking.dto.PrestataireRequestDto;
import com.kode_project.ebooking.dto.PrestataireResponseDto;
import com.kode_project.ebooking.dto.UserSummaryDto;
import com.kode_project.ebooking.entity.Prestataire;
import com.kode_project.ebooking.entity.User;
import com.kode_project.ebooking.exception.PrestataireNotFoundException;
import com.kode_project.ebooking.exception.UserNotFoundException;
import com.kode_project.ebooking.repository.PrestataireRepository;
import com.kode_project.ebooking.repository.ServiceRepository;
import com.kode_project.ebooking.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PrestataireServiceImpl implements PrestataireService {

    private final PrestataireRepository prestataireRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    @Override
    public List<PrestataireResponseDto> getAllPrestataire() {
        return prestataireRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PrestataireResponseDto getPrestataireById(Long id) {
        return entityToDto(prestataireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prestataire with id " + id + " not found")));
    }

    @Override
    public PrestataireResponseDto addPrestataire(PrestataireRequestDto prestataireRequestDto) {
        return entityToDto(prestataireRepository.save(dtoToEntity(prestataireRequestDto)));
    }

    @Override
    public PrestataireResponseDto updatePrestataire(PrestataireRequestDto prestataireRequestDto, Long id) {
        Prestataire prestataire = prestataireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prestataire introuvable"));

        User user = userRepository.getReferenceById(prestataireRequestDto.userId());
        com.kode_project.ebooking.entity.Service service = serviceRepository.getReferenceById(prestataireRequestDto.serviceId());

        prestataire.setUser(user);
        prestataire.setService(service);
        prestataire.setSpecialite(prestataireRequestDto.specialite());
        prestataire.setAdresse(prestataireRequestDto.adresse());

        return entityToDto(prestataireRepository.save(prestataire));
    }

    @Override
    public void deletePrestataire(Long id) {
        if (!prestataireRepository.existsById(id)) {
            throw new PrestataireNotFoundException("Prestataire avec l'id : " + id + " introuvable");
        }
        prestataireRepository.deleteById(id);
    }

    private PrestataireResponseDto entityToDto(Prestataire prestataire) {
        UserSummaryDto userDto = null;
        if (prestataire.getUser() == null) throw new UserNotFoundException("Prestataire n'a pas de compte utilisateur");

        userDto = new UserSummaryDto(
                prestataire.getUser().getUserId(),
                prestataire.getUser().getNom(),
                prestataire.getUser().getEmail()
        );
        return new PrestataireResponseDto(
                prestataire.getPrestataireId(),
                userDto,
                prestataire.getSpecialite(),
                prestataire.getAdresse()
        );
    }

    public Prestataire dtoToEntity(PrestataireRequestDto prestataireRequestDto) {
        User user = userRepository.getReferenceById(prestataireRequestDto.userId());
        com.kode_project.ebooking.entity.Service service = serviceRepository.getReferenceById(prestataireRequestDto.serviceId());

        return Prestataire.builder()
                .user(user)
                .specialite(prestataireRequestDto.specialite())
                .adresse(prestataireRequestDto.adresse())
                .service(service)
                .build();
    }
}
