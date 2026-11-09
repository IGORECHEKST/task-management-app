package task_management_app.service;

import task_management_app.dto.user.UserRegistrationRequestDto;
import task_management_app.dto.user.UserResponseDto;
import task_management_app.dto.user.UserRoleUpdateRequestDto;
import task_management_app.dto.user.UserUpdateProfileRequestDto;
import task_management_app.model.User;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto);

    UserResponseDto getProfile(User user);

    UserResponseDto updateProfile(User user, UserUpdateProfileRequestDto requestDto);

    UserResponseDto updateRole(Long userId, UserRoleUpdateRequestDto requestDto);
}