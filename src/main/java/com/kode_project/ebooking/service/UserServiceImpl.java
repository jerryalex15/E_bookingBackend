package com.kode_project.ebooking.service;

import com.kode_project.ebooking.dto.*;
import com.kode_project.ebooking.entity.Prestataire;
import com.kode_project.ebooking.entity.Role;
import com.kode_project.ebooking.entity.User;
import com.kode_project.ebooking.exception.EmailAlreadyExistException;
import com.kode_project.ebooking.exception.RoleNotFoundException;
import com.kode_project.ebooking.exception.UserNotFoundException;
import com.kode_project.ebooking.repository.PrestataireRepository;
import com.kode_project.ebooking.repository.RoleRepository;
import com.kode_project.ebooking.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PrestataireRepository prestataireRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("L'utilisateur n'existe pas"));
    }

    @Override
    public UserResponseDto registrationClient(UserRequestDto userRequestDto) {
        // À développer pour la validation et le sauvegarder de Role
        User user = buildUser(userRequestDto, "CLIENT");
        return entityToDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDto registrationPro(UserRequestDto userRequestDto) {
        User user = userRepository.save(buildUser(userRequestDto, "PRO"));
        Prestataire p = Prestataire.builder()
                .user(user)
                .build();
        Prestataire prestataire = prestataireRepository.save(p);
        user.setPrestataire(prestataire);
        return entityToDto(user);
    }

    private User buildUser(UserRequestDto dto, String roleName) {
        // Vérification unicité email
        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistException(dto.email());
        }

        Role role = roleRepository.findByRoleNom(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Rôle introuvable : " + roleName));
        User user = dtoToEntity(dto);
        user.setRole(role);
        return user;
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::entityToDto).collect(Collectors.toList());
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User avec l'id : " + id + " introuvable"));
        return entityToDto(user);
    }

    @Override
    public UserResponseDto updateUser(UserUpdateRequestDto userUpdateRequestDto, Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User avec l'id : " + id + " introuvable"));
        user.setPrenom(userUpdateRequestDto.prenom());
        user.setNom(userUpdateRequestDto.nom());
        user.setEmail(userUpdateRequestDto.email());
        user.setTelephone(userUpdateRequestDto.telephone());
        // motDePasse à gérer séparément avec encodage BCrypt
        if (userUpdateRequestDto.roleId() != null) {
            Role role = roleRepository.findById(userUpdateRequestDto.roleId())
                    .orElseThrow(() -> new RoleNotFoundException("Role avec id : "+ userUpdateRequestDto.roleId() + "introuvable"));
            user.setRole(role);
        }
        return entityToDto(userRepository.save(user));
    }

    @Override
    public void toggleStatutCompte(Long id, boolean activer) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User avec l'id : " + id + " introuvable"));
        user.setActiveStatut(activer);
        userRepository.save(user);
    }

    @Override
    public void changePassword(Long id, ChangePasswordDto changePasswordDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User avec l'id : " + id + " introuvable"));

        // Vérifier que l'ancien mot de passe est correct
        if (!passwordEncoder.matches(changePasswordDto.ancienMotDePasse(), user.getMotDePasse())) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect");
        }

        user.setMotDePasse(passwordEncoder.encode(changePasswordDto.nouveauMotDePasse()));
        userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    private User dtoToEntity(UserRequestDto userRequestDto) {
        return User.builder()
                .prenom(userRequestDto.prenom())
                .nom(userRequestDto.nom())
                .email(userRequestDto.email())
                .motDePasse(passwordEncoder.encode(userRequestDto.motDePasse()))
                .build();
    }

    private UserResponseDto entityToDto(User user) {
        RoleSummaryDto role = RoleSummaryDto.builder()
                .roleId(user.getRole().getRoleId())
                .roleNom(user.getRole().getRoleNom())
                .build();

        Long prestataireId = prestataireRepository
                .findByUserUserId(user.getUserId())
                .map(Prestataire::getPrestataireId)
                .orElse(null);

        return UserResponseDto.builder()
                .userId(user.getUserId())
                .activeStatut(user.isActiveStatut())
                .email(user.getEmail())
                .nom(user.getNom())
                .telephone(user.getTelephone())
                .prenom(user.getPrenom())
                .role(role)
                .prestataireId(prestataireId)
                .build();
    }
}
