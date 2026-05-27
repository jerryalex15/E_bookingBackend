package com.kode_project.ebooking.controller;

import com.kode_project.ebooking.dto.RoleSummaryDto;
import com.kode_project.ebooking.dto.UserResponseDto;
import com.kode_project.ebooking.entity.User;
import com.kode_project.ebooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class MeController {

    @Autowired
    UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getAuthenticatedUser(Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName();
        User user = userService.findUserByEmail(email);

        UserResponseDto userResponseDto = UserResponseDto.builder()
                .userId(user.getUserId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(email)
                .telephone(user.getTelephone())
                .activeStatut(user.isActiveStatut())
                .role(new RoleSummaryDto(user.getRole().getRoleId(), user.getRole().getRoleNom()))
                .prestataireId(user.getPrestataire() != null ? user.getPrestataire().getPrestataireId(): null)
                .build();

        return ResponseEntity.ok(userResponseDto);
    }
}
