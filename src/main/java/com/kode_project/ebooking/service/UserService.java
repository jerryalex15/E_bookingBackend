package com.kode_project.ebooking.service;

import com.kode_project.ebooking.dto.ChangePasswordDto;
import com.kode_project.ebooking.dto.UserRequestDto;
import com.kode_project.ebooking.dto.UserResponseDto;
import com.kode_project.ebooking.dto.UserUpdateRequestDto;
import com.kode_project.ebooking.entity.User;

import java.util.List;

public interface UserService {

    User findUserByEmail (String email);

    UserResponseDto registrationClient(UserRequestDto userRequestDto);

    UserResponseDto registrationPro(UserRequestDto userRequestDto);

    UserResponseDto getUserById(Long id);

    UserResponseDto updateUser(UserUpdateRequestDto userUpdateRequestDto, Long id);

    void deleteUserById(Long id);

    void changePassword(Long id, ChangePasswordDto changePasswordDto);

    void toggleStatutCompte(Long id, boolean activer);

    List<UserResponseDto> getAllUsers();
}
