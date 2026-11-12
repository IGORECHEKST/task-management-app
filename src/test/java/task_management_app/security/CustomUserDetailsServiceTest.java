package task_management_app.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import task_management_app.model.User;
import task_management_app.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
        user.setPassword("encoded");
    }

    @Test
    @DisplayName("loadUserByUsername: finds user by email")
    void loadUserByUsername_ByEmail_ReturnsUserDetails() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("john@example.com");

        assertNotNull(result);
        assertEquals("john_doe", result.getUsername());
    }

    @Test
    @DisplayName("loadUserByUsername: falls back to username when email not matched")
    void loadUserByUsername_ByUsername_ReturnsUserDetails() {
        when(userRepository.findByEmail("john_doe")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("john_doe");

        assertNotNull(result);
        assertEquals("john_doe", result.getUsername());
    }

    @Test
    @DisplayName("loadUserByUsername: throws UsernameNotFoundException when user does not exist")
    void loadUserByUsername_NotFound_ThrowsUsernameNotFoundException() {
        when(userRepository.findByEmail("unknown")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("unknown")
        );
    }
}

