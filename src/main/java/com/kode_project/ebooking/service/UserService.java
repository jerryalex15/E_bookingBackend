package com.kode_project.ebooking.service;

import com.kode_project.ebooking.dto.ChangePasswordDto;
import com.kode_project.ebooking.dto.UserRequestDto;
import com.kode_project.ebooking.dto.UserResponseDto;
import com.kode_project.ebooking.dto.UserUpdateRequestDto;

import java.util.List;

public interface UserService {
    UserResponseDto registration(UserRequestDto userRequestDto);

    UserResponseDto getUserById(Long id);

    UserResponseDto updateUser(UserUpdateRequestDto userUpdateRequestDto, Long id);

    void deleteUserById(Long id);

    void changePassword(Long id, ChangePasswordDto changePasswordDto);

    void toggleStatutCompte(Long id, boolean activer);

    List<UserResponseDto> getAllUsers();
}
