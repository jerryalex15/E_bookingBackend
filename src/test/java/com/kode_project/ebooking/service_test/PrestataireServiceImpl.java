package com.kode_project.ebooking.service_test;

import com.kode_project.ebooking.dto.PrestataireRequestDto;
import com.kode_project.ebooking.dto.PrestataireResponseDto;
import com.kode_project.ebooking.entity.Prestataire;
import com.kode_project.ebooking.entity.User;
import com.kode_project.ebooking.exception.PrestataireNotFoundException;
import com.kode_project.ebooking.exception.UserNotFoundException;
import com.kode_project.ebooking.repository.PrestataireRepository;
import com.kode_project.ebooking.repository.ServiceRepository;
import com.kode_project.ebooking.repository.UserRepository;
import com.kode_project.ebooking.service.PrestataireServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrestataireServiceImplTest {

    @Mock
    private PrestataireRepository prestataireRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private PrestataireServiceImpl prestataireService;

    // Fixtures
    private User user;
    private com.kode_project.ebooking.entity.Service service;
    private Prestataire prestataire;
    private PrestataireRequestDto requestDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setNom("Rakoto");
        user.setEmail("rakoto@mail.com");

        service = new com.kode_project.ebooking.entity.Service();
        service.setServiceId(10L);

        prestataire = Prestataire.builder()
                .prestataireId(100L)
                .user(user)
                .specialite("Plomberie")
                .adresse("12 rue des fleurs")
                .service(service)
                .build();

        requestDto = new PrestataireRequestDto(1L, "Plomberie", "12 rue des fleurs", 10L);
    }

    // getAllPrestataire

    @Nested
    @DisplayName("getAllPrestataire")
    class GetAllPrestataire {

        @Test
        @DisplayName("retourne la liste de tous les prestataires")
        void shouldReturnAllPrestataires() {
            when(prestataireRepository.findAll()).thenReturn(List.of(prestataire));

            List<PrestataireResponseDto> result = prestataireService.getAllPrestataire();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).prestataireId()).isEqualTo(100L);
            assertThat(result.get(0).user().nom()).isEqualTo("Rakoto");
            verify(prestataireRepository).findAll();
        }

        @Test
        @DisplayName("retourne une liste vide si aucun prestataire")
        void shouldReturnEmptyList() {
            when(prestataireRepository.findAll()).thenReturn(List.of());

            List<PrestataireResponseDto> result = prestataireService.getAllPrestataire();

            assertThat(result).isEmpty();
        }
    }

    // getPrestataireById

    @Nested
    @DisplayName("getPrestataireById")
    class GetPrestataireById {

        @Test
        @DisplayName("retourne le prestataire correspondant à l'id")
        void shouldReturnPrestataire_whenFound() {
            when(prestataireRepository.findById(100L)).thenReturn(Optional.of(prestataire));

            PrestataireResponseDto result = prestataireService.getPrestataireById(100L);

            assertThat(result.prestataireId()).isEqualTo(100L);
            assertThat(result.specialite()).isEqualTo("Plomberie");
        }

        @Test
        @DisplayName("lève RuntimeException si l'id est introuvable")
        void shouldThrow_whenNotFound() {
            when(prestataireRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> prestataireService.getPrestataireById(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("999");
        }
    }

    // addPrestataire

    @Nested
    @DisplayName("addPrestataire")
    class AddPrestataire {

        @Test
        @DisplayName("crée et retourne un nouveau prestataire")
        void shouldSaveAndReturnPrestataire() {
            when(userRepository.getReferenceById(1L)).thenReturn(user);
            when(serviceRepository.getReferenceById(10L)).thenReturn(service);
            when(prestataireRepository.save(any(Prestataire.class))).thenReturn(prestataire);

            PrestataireResponseDto result = prestataireService.addPrestataire(requestDto);

            assertThat(result.prestataireId()).isEqualTo(100L);
            assertThat(result.adresse()).isEqualTo("12 rue des fleurs");
            verify(prestataireRepository).save(any(Prestataire.class));
        }
    }

    // updatePrestataire

    @Nested
    @DisplayName("updatePrestataire")
    class UpdatePrestataire {

        @Test
        @DisplayName("met à jour et retourne le prestataire modifié")
        void shouldUpdateAndReturn() {
            when(prestataireRepository.findById(100L)).thenReturn(Optional.of(prestataire));
            when(userRepository.getReferenceById(1L)).thenReturn(user);
            when(serviceRepository.getReferenceById(10L)).thenReturn(service);
            when(prestataireRepository.save(prestataire)).thenReturn(prestataire);

            PrestataireRequestDto updateDto =
                    new PrestataireRequestDto(1L, "Électricité", "5 avenue de la paix", 10L);

            PrestataireResponseDto result = prestataireService.updatePrestataire(updateDto, 100L);

            assertThat(result).isNotNull();
            verify(prestataireRepository).save(prestataire);
        }

        @Test
        @DisplayName("lève RuntimeException si le prestataire à mettre à jour est introuvable")
        void shouldThrow_whenNotFound() {
            when(prestataireRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> prestataireService.updatePrestataire(requestDto, 999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("introuvable");
        }
    }

    // deletePrestataire

    @Nested
    @DisplayName("deletePrestataire")
    class DeletePrestataire {

        @Test
        @DisplayName("supprime le prestataire existant sans erreur")
        void shouldDelete_whenExists() {
            when(prestataireRepository.existsById(100L)).thenReturn(true);

            prestataireService.deletePrestataire(100L);

            verify(prestataireRepository).deleteById(100L);
        }

        @Test
        @DisplayName("lève PrestataireNotFoundException si l'id est introuvable")
        void shouldThrow_whenNotFound() {
            when(prestataireRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> prestataireService.deletePrestataire(999L))
                    .isInstanceOf(PrestataireNotFoundException.class)
                    .hasMessageContaining("999");

            verify(prestataireRepository, never()).deleteById(any());
        }
    }

    // entityToDto (cas limites)
    @Nested
    @DisplayName("entityToDto — cas limites")
    class EntityToDto {

        @Test
        @DisplayName("lève UserNotFoundException si le prestataire n'a pas d'utilisateur")
        void shouldThrow_whenUserIsNull() {
            Prestataire withoutUser = Prestataire.builder()
                    .prestataireId(200L)
                    .user(null)
                    .specialite("Peinture")
                    .adresse("Bagneux")
                    .service(service)
                    .build();

            when(prestataireRepository.findAll()).thenReturn(List.of(withoutUser));

            assertThatThrownBy(() -> prestataireService.getAllPrestataire())
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("serviceId est null si le prestataire n'a pas de service associé")
        void shouldReturnNullServiceId_whenServiceIsNull() {
            Prestataire withoutService = Prestataire.builder()
                    .prestataireId(300L)
                    .user(user)
                    .specialite("Jardinage")
                    .adresse("Paris")
                    .service(null)
                    .build();

            when(prestataireRepository.findAll()).thenReturn(List.of(withoutService));

            List<PrestataireResponseDto> result = prestataireService.getAllPrestataire();

            assertThat(result.get(0).serviceId()).isNull();
        }
    }
}