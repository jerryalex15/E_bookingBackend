package com.kode_project.ebooking.service;

import com.kode_project.ebooking.dto.*;
import com.kode_project.ebooking.entity.Disponibilite;
import com.kode_project.ebooking.entity.Prestataire;
import com.kode_project.ebooking.entity.RendezVous;
import com.kode_project.ebooking.entity.User;
import com.kode_project.ebooking.exception.DisponibiliteNotFoundException;
import com.kode_project.ebooking.exception.RendezVousNotAvailableException;
import com.kode_project.ebooking.exception.RendezVousNotFoundException;
import com.kode_project.ebooking.repository.DisponibiliteRepository;
import com.kode_project.ebooking.repository.PrestataireRepository;
import com.kode_project.ebooking.repository.RendezVousRepository;
import com.kode_project.ebooking.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RendezVousServiceImpl implements RendezVousService {
    private static final int DUREE_CONSULTATION_EN_HEURE = 1;


    private final RendezVousRepository rendezVousRepository;
    private final DisponibiliteRepository disponibiliteRepository;
    private final UserRepository userRepository;
    private final PrestataireRepository prestataireRepository;

    @Override
    public RendezVousResponseDto takeAppointment(RendezVousRequestDto rendezVousRequestDto) {
        ZonedDateTime dateHeureDebutUTC = rendezVousRequestDto.dateHeure().withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime dateHeureFinUTC = dateHeureDebutUTC.plusHours(DUREE_CONSULTATION_EN_HEURE);

        LocalDate dateUTC = dateHeureDebutUTC.toLocalDate();
        LocalTime heureDebut = dateHeureDebutUTC.toLocalTime();
        LocalTime heureFin = dateHeureFinUTC.toLocalTime();

        // Vérifie si le prestataire dispose une disponibilité pour le rendez-vous choisi
        List<Disponibilite> disponibilites = disponibiliteRepository
                .findPrestataireDisponibilities(rendezVousRequestDto.prestataireId(), dateUTC, heureDebut, heureFin);
        if (disponibilites.isEmpty()) {
            throw new DisponibiliteNotFoundException("Le prestataire n'a aucune disponibilité le " + dateUTC + " à " +
                    heureDebut + " - " + heureFin);
        }

        // Vérifie si le rendez est encore dispo
        List<RendezVous> rendezVousList = rendezVousRepository.
                findOverlappingRendezVous(rendezVousRequestDto.prestataireId(), dateUTC, heureDebut, heureFin);
        if (!rendezVousList.isEmpty()) {
            throw new RendezVousNotAvailableException("Ce créneau de rendez-vous est deja pris");
        }

        return entityToDto(rendezVousRepository.save(dtoToEntity(rendezVousRequestDto)));
    }

    @Override
    public List<RendezVousResponseDto> getAppointmentByClientId(Long id) {
        return rendezVousRepository.findByUserUserId(id).stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RendezVousResponseDto> getAppointmentByPrestataireId(Long id) {
        return rendezVousRepository.findByPrestatairePrestataireId(id).stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public RendezVousResponseDto updateAppointmentById(Long id, RendezVousUpdateDto rendezVousUpdateDto) {
        RendezVous rendezVous = rendezVousRepository.findById(id)
                .orElseThrow(() -> new RendezVousNotFoundException("Rendez-vous introuvable avec l'id : " + id));

        // Mise à jour du statut uniquement
        if (rendezVousUpdateDto.dateHeure() == null && rendezVousUpdateDto.prestataireId() == null) {
            rendezVous.setStatut(rendezVousUpdateDto.statut());
            return entityToDto(rendezVousRepository.save(rendezVous));
        }

        // Calcul des nouvelles heures
        assert rendezVousUpdateDto.dateHeure() != null;
        ZonedDateTime dateHeureDebutUTC = rendezVousUpdateDto.dateHeure().withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime dateHeureFinUTC = dateHeureDebutUTC.plusHours(DUREE_CONSULTATION_EN_HEURE);

        LocalDate dateUTC = dateHeureDebutUTC.toLocalDate();
        LocalTime heureDebut = dateHeureDebutUTC.toLocalTime();
        LocalTime heureFin = dateHeureFinUTC.toLocalTime();

        // si le prestataire va changer ou pas
        Long prestataireId = rendezVousUpdateDto.prestataireId() != null
                ? rendezVousUpdateDto.prestataireId()
                : rendezVous.getPrestataire().getPrestataireId();

        // Vérifier la disponibilité du prestataire
        List<Disponibilite> disponibilites = disponibiliteRepository
                .findPrestataireDisponibilities(prestataireId, dateUTC, heureDebut, heureFin);
        if (disponibilites.isEmpty()) {
            throw new DisponibiliteNotFoundException("Le prestataire n'a aucune disponibilité le " + dateUTC + " à " +
                    heureDebut + " - " + heureFin);
        }

        // Vérifier les chevauchements (en excluant le rendez-vous actuel)
        List<RendezVous> overlapping = rendezVousRepository
                .findOverlappingRendezVousExcluding(prestataireId, dateUTC, heureDebut, heureFin, id);
        if (!overlapping.isEmpty()) {
            throw new RendezVousNotAvailableException("Ce créneau de rendez-vous est déjà pris");
        }

        // Appliquer les modifications
        if (rendezVousUpdateDto.prestataireId() != null) {
            Prestataire prestataire = prestataireRepository.getReferenceById(rendezVousUpdateDto.prestataireId());
            rendezVous.setPrestataire(prestataire);
        }
        rendezVous.setDate(dateUTC);
        rendezVous.setHeureDebut(heureDebut);
        rendezVous.setHeureFin(heureDebut.plusHours(DUREE_CONSULTATION_EN_HEURE));
        if (rendezVousUpdateDto.statut() != null) {
            rendezVous.setStatut(rendezVousUpdateDto.statut());
        }

        return entityToDto(rendezVousRepository.save(rendezVous));
    }

    @Override
    public void deleteAppointmentById(Long id) {
        if (!rendezVousRepository.existsById(id)) {
            throw new RendezVousNotFoundException("Rendez-vous introuvable avec l'id : " + id);
        }
        rendezVousRepository.deleteById(id);
    }

    private RendezVous dtoToEntity(RendezVousRequestDto rendezVousRequestDto) {
        User user = userRepository.getReferenceById(rendezVousRequestDto.userId());
        Prestataire prestataire = prestataireRepository.getReferenceById(rendezVousRequestDto.prestataireId());
        ZonedDateTime dateHeureDebutUTC = rendezVousRequestDto.dateHeure().withZoneSameInstant(ZoneOffset.UTC);
        LocalDate dateUTC = dateHeureDebutUTC.toLocalDate();
        LocalTime heure = dateHeureDebutUTC.toLocalTime();

        return RendezVous.builder()
                .date(dateUTC)
                .heureDebut(heure)
                .heureFin(heure.plusHours(DUREE_CONSULTATION_EN_HEURE))
                .user(user)
                .prestataire(prestataire)
                .build();
    }

    private RendezVousResponseDto entityToDto(RendezVous rendezVous) {
        User user = rendezVous.getUser();
        Prestataire prestataire = rendezVous.getPrestataire();
        UserSummaryDto userDto = (user == null) ? null : new UserSummaryDto(
                user.getUserId(),
                user.getNom(),
                user.getEmail()
        );
        PrestataireSummaryDto prestataireDto = (prestataire == null) ? null : new PrestataireSummaryDto(
                prestataire.getPrestataireId(),
                prestataire.getSpecialite(),
                prestataire.getAdresse()
        );
        return new RendezVousResponseDto(
                rendezVous.getRendezVousId(),
                userDto,
                prestataireDto,
                rendezVous.getDate(),
                rendezVous.getHeureDebut(),
                rendezVous.getHeureFin(),
                rendezVous.getStatut()
        );
    }
}
