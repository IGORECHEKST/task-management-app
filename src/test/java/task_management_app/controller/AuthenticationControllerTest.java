package task_management_app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import task_management_app.dto.user.UserLoginRequestDto;
import task_management_app.dto.user.UserLoginResponseDto;
import task_management_app.dto.user.UserRegistrationRequestDto;
import task_management_app.dto.user.UserResponseDto;
import task_management_app.model.Role;
import task_management_app.security.AuthenticationService;
import task_management_app.service.UserService;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
    }

    @Test
    @DisplayName("POST /api/auth/register: registers a new user and returns 201 Created")
    void register_ValidRequest_ReturnsCreated() throws Exception {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setUsername("john_doe");
        requestDto.setEmail("john@example.com");
        requestDto.setPassword("password123");
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(1L);
        responseDto.setUsername("john_doe");
        responseDto.setEmail("john@example.com");
        responseDto.setFirstName("John");
        responseDto.setLastName("Doe");
        responseDto.setRoles(Set.of(Role.RoleName.ROLE_USER));

        when(userService.register(any(UserRegistrationRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService).register(any(UserRegistrationRequestDto.class));
    }

    @Test
    @DisplayName("POST /api/auth/login: authenticates user and returns JWT token")
    void login_ValidCredentials_ReturnsToken() throws Exception {
        UserLoginRequestDto requestDto = new UserLoginRequestDto();
        requestDto.setEmail("john@example.com");
        requestDto.setPassword("password123");

        UserLoginResponseDto responseDto = new UserLoginResponseDto("jwt-token-value");

        when(authenticationService.authenticate(any(UserLoginRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-value"));

        verify(authenticationService).authenticate(any(UserLoginRequestDto.class));
    }
}

