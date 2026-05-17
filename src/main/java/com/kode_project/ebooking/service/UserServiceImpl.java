package com.kode_project.ebooking.service;

import com.kode_project.ebooking.dto.*;
import com.kode_project.ebooking.entity.Role;
import com.kode_project.ebooking.entity.User;
import com.kode_project.ebooking.exception.UserNotFoundException;
import com.kode_project.ebooking.repository.RoleRepository;
import com.kode_project.ebooking.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto registration(UserRequestDto userRequestDto) {
        // À développer pour la validation et le sauvegarder de Role
        // penser à affecter le rôle (en fonction de l'URL peut-être)
        return entityToDto(userRepository.save(dtoToEntity(userRequestDto)));
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
        if (userUpdateRequestDto.roleIds() != null && !userUpdateRequestDto.roleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(userUpdateRequestDto.roleIds()));
            user.setRoles(roles);
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

    private User dtoToEntity(UserRequestDto dto) {
        Set<Role> roles = new HashSet<>(
                roleRepository.findAllById(dto.roleIds())
        );

        return User.builder()
                .prenom(dto.prenom())
                .nom(dto.nom())
                .email(dto.email())
                .telephone(dto.telephone())
                .motDePasse(passwordEncoder.encode(dto.motDePasse()))
                .roles(roles)
                .build();
    }

    private UserResponseDto entityToDto(User user) {
        Set<RoleSummaryDto> roles = user.getRoles()
                .stream()
                .map(
                        r -> RoleSummaryDto.builder()
                                .roleId(r.getRoleId())
                                .nomRole(r.getRoleNom())
                                .build()
                )
                .collect(Collectors.toSet());

        return UserResponseDto.builder()
                .userId(user.getUserId())
                .activeStatut(user.isActiveStatut())
                .email(user.getEmail())
                .nom(user.getNom())
                .telephone(user.getTelephone())
                .prenom(user.getPrenom())
                .roles(roles)
                .build();
    }
}
