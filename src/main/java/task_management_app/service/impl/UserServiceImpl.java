package task_management_app.service.impl;

import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import task_management_app.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("User with email " + requestDto.getEmail() + " already exists");
        }
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new RegistrationException("User with username " + requestDto.getUsername() + " already exists");
        }

        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        Role defaultRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> new EntityNotFoundException("Can't find role: " + Role.RoleName.ROLE_USER));
        user.setRoles(new HashSet<>(Set.of(defaultRole)));

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getProfile(User user) {
        User currentUser = findCurrentUser(user);
        return userMapper.toDto(currentUser);
    }

    @Override
    @Transactional
    public UserResponseDto updateProfile(User user, UserUpdateProfileRequestDto requestDto) {
        User currentUser = findCurrentUser(user);

        if (!currentUser.getEmail().equals(requestDto.getEmail())
                && userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("User with email " + requestDto.getEmail() + " already exists");
        }
        if (!currentUser.getUsername().equals(requestDto.getUsername())
                && userRepository.existsByUsername(requestDto.getUsername())) {
            throw new RegistrationException("User with username " + requestDto.getUsername() + " already exists");
        }

        userMapper.updateModelFromDto(requestDto, currentUser);
        User savedUser = userRepository.save(currentUser);
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public UserResponseDto updateRole(Long userId, UserRoleUpdateRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find user by id: " + userId));

        Set<Role.RoleName> roleNames = requestDto.getRoles();
        if (roleNames == null || roleNames.isEmpty()) {
            if (requestDto.getRole() != null) {
                roleNames = Set.of(requestDto.getRole());
            } else {
                throw new IllegalArgumentException("Role must be specified");
            }
        }

        Set<Role> roles = new HashSet<>();
        for (Role.RoleName roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new EntityNotFoundException("Can't find role: " + roleName));
            roles.add(role);
        }

        user.setRoles(roles);
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    private User findCurrentUser(User user) {
        if (user.getId() != null) {
            return userRepository.findById(user.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Can't find user by id: " + user.getId()));
        }
        if (user.getUsername() != null) {
            return userRepository.findByUsername(user.getUsername())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Can't find user by username: " + user.getUsername()));
        }
        return user;
    }
}
