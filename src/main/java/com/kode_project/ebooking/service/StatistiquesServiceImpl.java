package com.kode_project.ebooking.service;

import com.kode_project.ebooking.dto.StatistiquesResponseDto;
import com.kode_project.ebooking.enums.Statut;
import com.kode_project.ebooking.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StatistiquesServiceImpl implements StatistiquesService {

    private final RendezVousRepository rendezVousRepository;
    private final DisponibiliteRepository disponibiliteRepository;
    private final UserRepository userRepository;
    private final PrestataireRepository prestataireRepository;
    private final ServiceRepository serviceRepository;

    @Override
    public StatistiquesResponseDto getStatistiques() {
        long total = rendezVousRepository.count();
        long confirmes = rendezVousRepository.countByStatut(Statut.CONFIRME);
        long annules = rendezVousRepository.countByStatut(Statut.ANNULE);
        long enAttente = rendezVousRepository.countByStatut(Statut.EN_ATTENTE);
        long totalDisponibilites = disponibiliteRepository.countTotalDisponibilites();

        double tauxOccupation = totalDisponibilites == 0 ? 0 :
                (double) confirmes / totalDisponibilites * 100;

        return new StatistiquesResponseDto(
                total,
                confirmes,
                annules,
                enAttente,
                Math.round(tauxOccupation * 100.0) / 100.0,  // arrondi 2 décimales
                userRepository.countUtilisateursActifs(),
                prestataireRepository.count(),
                serviceRepository.count()
        );
    }
}