package com.kode_project.ebooking.service;

import com.kode_project.ebooking.dto.DisponibiliteRequestDto;
import com.kode_project.ebooking.dto.DisponibiliteResponseDto;
import com.kode_project.ebooking.dto.PrestataireSummaryDto;
import com.kode_project.ebooking.entity.Disponibilite;
import com.kode_project.ebooking.entity.Prestataire;
import com.kode_project.ebooking.enums.JourSemaine;
import com.kode_project.ebooking.exception.DisponibiliteConflitException;
import com.kode_project.ebooking.exception.DisponibiliteNotFoundException;
import com.kode_project.ebooking.repository.DisponibiliteRepository;
import com.kode_project.ebooking.repository.PrestataireRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.kode_project.ebooking.service.FrenchEnglishDayDictionary.DAY_OF_WEEK_MAP;

@Service
@AllArgsConstructor
public class DisponibiliteServiceImpl implements DisponibiliteService {

    private final DisponibiliteRepository disponibiliteRepository;
    private final PrestataireRepository prestataireRepository;

    @Override
    public List<DisponibiliteResponseDto> getPrestataireDisponibility(Long prestataireId) {
        return disponibiliteRepository.findByPrestatairePrestataireId(prestataireId).stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public DisponibiliteResponseDto addDisponibility(DisponibiliteRequestDto disponibiliteRequestDto) {
        // Faire un temps en UTC
        ZonedDateTime dateHeureDebutUTC = disponibiliteRequestDto.dateHeureDebut().withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime dateHeureFinUTC = disponibiliteRequestDto.dateHeureFin().withZoneSameInstant(ZoneOffset.UTC);


        if (dateHeureFinUTC.equals(dateHeureDebutUTC) || dateHeureFinUTC.isBefore(dateHeureDebutUTC)) {
            throw new IllegalArgumentException("L'heure de fin doit être après l'heure de début");
        }

        LocalDate dateUTC = dateHeureDebutUTC.toLocalDate();
        LocalTime heureDebut = dateHeureDebutUTC.toLocalTime();
        LocalTime heureFin = dateHeureFinUTC.toLocalTime();

        List<Disponibilite> disponibilites = disponibiliteRepository.findByPrestatairePrestataireId(disponibiliteRequestDto.prestataireId());
        boolean conflit = disponibilites.stream()
                .filter(d -> d.getDate().equals(dateUTC))
                .anyMatch(d -> heureDebut.isBefore(d.getHeureFin()) && heureFin.isAfter(d.getHeureDebut()));

        if (conflit) {
            throw new DisponibiliteConflitException(
                    "Créneau en conflit le " + dateUTC + " entre " + heureDebut + " et " + heureFin
            );
        }

        return entityToDto(disponibiliteRepository.save(dtoToEntity(disponibiliteRequestDto)));
    }

    @Override
    public DisponibiliteResponseDto updateDisponibility(DisponibiliteRequestDto disponibiliteRequestDto, Long id) {
        ZonedDateTime dateHeureDebutUTC = disponibiliteRequestDto.dateHeureDebut().withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime dateHeureFinUTC = disponibiliteRequestDto.dateHeureFin().withZoneSameInstant(ZoneOffset.UTC);


        if (dateHeureFinUTC.equals(dateHeureDebutUTC) || dateHeureFinUTC.isBefore(dateHeureDebutUTC)) {
            throw new IllegalArgumentException("L'heure de fin doit être après l'heure de début");
        }

        LocalDate dateUTC = dateHeureDebutUTC.toLocalDate();
        LocalTime heureDebut = dateHeureDebutUTC.toLocalTime();
        LocalTime heureFin = dateHeureFinUTC.toLocalTime();

        List<Disponibilite> disponibilites = disponibiliteRepository.findByPrestatairePrestataireId(disponibiliteRequestDto.prestataireId());
        boolean conflit = disponibilites.stream()
                .filter(d -> d.getDate().equals(dateUTC))
                .filter(d -> !d.getDisponibiliteId().equals(id))
                .anyMatch(d -> heureDebut.isBefore(d.getHeureFin()) && heureFin.isAfter(d.getHeureDebut()));
        if (conflit) {
            throw new DisponibiliteConflitException(
                    "Créneau en conflit le " + dateUTC + " entre " + heureDebut + " et " + heureFin
            );
        }

        Disponibilite disponibilite = disponibiliteRepository.findById(id)
                .orElseThrow(() -> new DisponibiliteNotFoundException("Disponibilité introuvable"));

        disponibilite.setDate(dateUTC);
        disponibilite.setHeureDebut(heureDebut);
        disponibilite.setHeureFin(heureFin);
        DayOfWeek dayOfWeek = dateUTC.getDayOfWeek();
        JourSemaine jour = DAY_OF_WEEK_MAP.get(dayOfWeek);
        disponibilite.setJourSemaine(jour);

        Prestataire prestataire = prestataireRepository.getReferenceById(disponibiliteRequestDto.prestataireId());
        disponibilite.setPrestataire(prestataire);

        return entityToDto(disponibiliteRepository.save(disponibilite));
    }

    @Override
    public void deleteDisponibility(Long id) {
        if (!disponibiliteRepository.existsById(id)) {
            throw new DisponibiliteNotFoundException("Disponibilité introuvable avec l'id : " + id);
        }
        disponibiliteRepository.deleteById(id);
    }

    private DisponibiliteResponseDto entityToDto(Disponibilite disponibilite) {
        PrestataireSummaryDto prestataireDto = new PrestataireSummaryDto(
                disponibilite.getPrestataire().getPrestataireId(),
                disponibilite.getPrestataire().getSpecialite(),
                disponibilite.getPrestataire().getAdresse()
        );

        return new DisponibiliteResponseDto(
                disponibilite.getDisponibiliteId(),
                prestataireDto,
                disponibilite.getDate(),
                disponibilite.getJourSemaine(),
                disponibilite.getHeureDebut(),
                disponibilite.getHeureFin()
        );
    }

    private Disponibilite dtoToEntity(DisponibiliteRequestDto disponibiliteRequestDto) {
        ZonedDateTime dateHeureDebutUTC = disponibiliteRequestDto.dateHeureDebut().withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime dateHeureFinUTC = disponibiliteRequestDto.dateHeureFin().withZoneSameInstant(ZoneOffset.UTC);
        LocalDate date = dateHeureDebutUTC.toLocalDate();
        Prestataire prestataire = prestataireRepository.getReferenceById(disponibiliteRequestDto.prestataireId());
        return Disponibilite.builder()
                .prestataire(prestataire)
                .date(date)
                .heureDebut(dateHeureDebutUTC.toLocalTime())
                .heureFin(dateHeureFinUTC.toLocalTime())
                .jourSemaine(DAY_OF_WEEK_MAP.get(date.getDayOfWeek()))
                .build();
    }
}
