package task_management_app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import task_management_app.dto.user.UserRegistrationRequestDto;
import task_management_app.dto.user.UserResponseDto;
import task_management_app.dto.user.UserRoleUpdateRequestDto;
import task_management_app.dto.user.UserUpdateProfileRequestDto;
import task_management_app.exception.EntityNotFoundException;
import task_management_app.exception.RegistrationException;
import task_management_app.mapper.UserMapper;
import task_management_app.model.Role;
import task_management_app.model.User;
import task_management_app.repository.RoleRepository;
import task_management_app.repository.UserRepository;
import task_management_app.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Role userRole;
    private Role adminRole;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        userRole = new Role(Role.RoleName.ROLE_USER);
        userRole.setId(1L);

        adminRole = new Role(Role.RoleName.ROLE_ADMIN);
        adminRole.setId(2L);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRoles(Set.of(userRole));

        userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setUsername("testuser");
        userResponseDto.setEmail("test@example.com");
        userResponseDto.setFirstName("Test");
        userResponseDto.setLastName("User");
        userResponseDto.setRoles(Set.of(Role.RoleName.ROLE_USER));
    }

    @Test
    @DisplayName("register: successfully registers a new user with default ROLE_USER")
    void register_ValidRequest_Success() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setUsername("newuser");
        requestDto.setEmail("new@example.com");
        requestDto.setPassword("password123");
        requestDto.setFirstName("New");
        requestDto.setLastName("User");

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(requestDto.getUsername())).thenReturn(false);
        when(userMapper.toModel(requestDto)).thenReturn(new User());
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName(Role.RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.register(requestDto);

        assertNotNull(result);
        assertEquals(userResponseDto.getUsername(), result.getUsername());
        verify(roleRepository).findByName(Role.RoleName.ROLE_USER);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register: throws RegistrationException when email already exists")
    void register_DuplicateEmail_ThrowsRegistrationException() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("existing@example.com");

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        assertThrows(RegistrationException.class, () -> userService.register(requestDto));
    }

    @Test
    @DisplayName("register: throws RegistrationException when username already exists")
    void register_DuplicateUsername_ThrowsRegistrationException() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("unique@example.com");
        requestDto.setUsername("existinguser");

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(requestDto.getUsername())).thenReturn(true);

        assertThrows(RegistrationException.class, () -> userService.register(requestDto));
    }

    @Test
    @DisplayName("getProfile: returns profile for current user")
    void getProfile_ValidUser_ReturnsProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.getProfile(user);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    @DisplayName("updateProfile: successfully updates user profile")
    void updateProfile_ValidRequest_Success() {
        UserUpdateProfileRequestDto requestDto = new UserUpdateProfileRequestDto();
        requestDto.setUsername("updateduser");
        requestDto.setEmail("updated@example.com");
        requestDto.setFirstName("Updated");
        requestDto.setLastName("User");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(requestDto.getUsername())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.updateProfile(user, requestDto);

        assertNotNull(result);
        verify(userMapper).updateModelFromDto(requestDto, user);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("updateProfile: throws RegistrationException when new email is already taken")
    void updateProfile_DuplicateEmail_ThrowsRegistrationException() {
        UserUpdateProfileRequestDto requestDto = new UserUpdateProfileRequestDto();
        requestDto.setEmail("taken@example.com");
        requestDto.setUsername("testuser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        assertThrows(RegistrationException.class, () -> userService.updateProfile(user, requestDto));
    }

    @Test
    @DisplayName("updateProfile: throws RegistrationException when new username is already taken")
    void updateProfile_DuplicateUsername_ThrowsRegistrationException() {
        UserUpdateProfileRequestDto requestDto = new UserUpdateProfileRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setUsername("takenuser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(requestDto.getUsername())).thenReturn(true);

        assertThrows(RegistrationException.class, () -> userService.updateProfile(user, requestDto));
    }

    @Test
    @DisplayName("updateRole: successfully updates user role to ROLE_ADMIN")
    void updateRole_ValidRole_Success() {
        UserRoleUpdateRequestDto requestDto = new UserRoleUpdateRequestDto(Role.RoleName.ROLE_ADMIN);

        UserResponseDto adminResponseDto = new UserResponseDto();
        adminResponseDto.setId(1L);
        adminResponseDto.setUsername("testuser");
        adminResponseDto.setRoles(Set.of(Role.RoleName.ROLE_ADMIN));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(Role.RoleName.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(adminResponseDto);

        UserResponseDto result = userService.updateRole(1L, requestDto);

        assertNotNull(result);
        assertTrue(result.getRoles().contains(Role.RoleName.ROLE_ADMIN));
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("updateRole: throws EntityNotFoundException when user does not exist")
    void updateRole_UserNotFound_ThrowsEntityNotFoundException() {
        UserRoleUpdateRequestDto requestDto = new UserRoleUpdateRequestDto(Role.RoleName.ROLE_ADMIN);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateRole(999L, requestDto));
    }

    @Test
    @DisplayName("updateRole: throws EntityNotFoundException when role does not exist")
    void updateRole_RoleNotFound_ThrowsEntityNotFoundException() {
        UserRoleUpdateRequestDto requestDto = new UserRoleUpdateRequestDto(Role.RoleName.ROLE_ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(Role.RoleName.ROLE_ADMIN)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateRole(1L, requestDto));
    }
}
