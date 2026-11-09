package task_management_app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import task_management_app.dto.user.UserResponseDto;
import task_management_app.dto.user.UserRoleUpdateRequestDto;
import task_management_app.dto.user.UserUpdateProfileRequestDto;
import task_management_app.model.User;
import task_management_app.service.UserService;

@Tag(name = "User Management", description = "Endpoints for managing users and profile")
@RestController
@RequestMapping({"/users", "/api/users"})
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user role", description = "Assign a new role to the user (Admin only)")
    public UserResponseDto updateRole(
            @PathVariable Long id,
            @RequestBody @Valid UserRoleUpdateRequestDto requestDto
    ) {
        return userService.updateRole(id, requestDto);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Retrieve profile of the authenticated user")
    public UserResponseDto getProfile(@AuthenticationPrincipal User user) {
        return userService.getProfile(user);
    }

    @PutMapping("/me")
    @Operation(summary = "Update user profile", description = "Update profile of the authenticated user")
    public UserResponseDto updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid UserUpdateProfileRequestDto requestDto
    ) {
        return userService.updateProfile(user, requestDto);
    }

    @PatchMapping("/me")
    @Operation(summary = "Patch user profile", description = "Update profile of the authenticated user")
    public UserResponseDto patchProfile(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid UserUpdateProfileRequestDto requestDto
    ) {
        return userService.updateProfile(user, requestDto);
    }
}
