package task_management_app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import task_management_app.dto.user.UserResponseDto;
import task_management_app.dto.user.UserRoleUpdateRequestDto;
import task_management_app.dto.user.UserUpdateProfileRequestDto;
import task_management_app.model.Role;
import task_management_app.model.User;
import task_management_app.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;
    private User testUser;
    private UserResponseDto testResponseDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        testResponseDto = new UserResponseDto();
        testResponseDto.setId(1L);
        testResponseDto.setUsername("testuser");
        testResponseDto.setEmail("test@example.com");
        testResponseDto.setFirstName("Test");
        testResponseDto.setLastName("User");
        testResponseDto.setRoles(Set.of(Role.RoleName.ROLE_USER));

        HandlerMethodArgumentResolver authPrincipalResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
            }

            @Override
            public Object resolveArgument(
                    MethodParameter parameter,
                    ModelAndViewContainer mavContainer,
                    NativeWebRequest webRequest,
                    WebDataBinderFactory binderFactory
            ) {
                return testUser;
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(authPrincipalResolver)
                .build();
    }

    @Test
    @DisplayName("GET /users/me: returns profile for authenticated user")
    void getProfile_ReturnsProfile() throws Exception {
        when(userService.getProfile(any(User.class))).thenReturn(testResponseDto);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService).getProfile(any(User.class));
    }

    @Test
    @DisplayName("GET /api/users/me: returns profile for authenticated user via /api prefix")
    void getProfile_ApiPrefix_ReturnsProfile() throws Exception {
        when(userService.getProfile(any(User.class))).thenReturn(testResponseDto);

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userService).getProfile(any(User.class));
    }

    @Test
    @DisplayName("PUT /users/me: updates user profile")
    void updateProfile_ValidRequest_Success() throws Exception {
        UserUpdateProfileRequestDto requestDto = new UserUpdateProfileRequestDto();
        requestDto.setUsername("updateduser");
        requestDto.setEmail("updated@example.com");
        requestDto.setFirstName("Updated");
        requestDto.setLastName("User");

        when(userService.updateProfile(any(User.class), any(UserUpdateProfileRequestDto.class)))
                .thenReturn(testResponseDto);

        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(userService).updateProfile(any(User.class), any(UserUpdateProfileRequestDto.class));
    }

    @Test
    @DisplayName("PATCH /users/me: patches user profile")
    void patchProfile_ValidRequest_Success() throws Exception {
        UserUpdateProfileRequestDto requestDto = new UserUpdateProfileRequestDto();
        requestDto.setUsername("patcheduser");
        requestDto.setEmail("patched@example.com");
        requestDto.setFirstName("Patched");
        requestDto.setLastName("User");

        when(userService.updateProfile(any(User.class), any(UserUpdateProfileRequestDto.class)))
                .thenReturn(testResponseDto);

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(userService).updateProfile(any(User.class), any(UserUpdateProfileRequestDto.class));
    }

    @Test
    @DisplayName("PUT /users/{id}/role: updates user role")
    void updateRole_ValidRequest_Success() throws Exception {
        UserRoleUpdateRequestDto requestDto = new UserRoleUpdateRequestDto(Role.RoleName.ROLE_ADMIN);

        UserResponseDto updatedRoleDto = new UserResponseDto();
        updatedRoleDto.setId(1L);
        updatedRoleDto.setUsername("testuser");
        updatedRoleDto.setRoles(Set.of(Role.RoleName.ROLE_ADMIN));

        when(userService.updateRole(eq(1L), any(UserRoleUpdateRequestDto.class)))
                .thenReturn(updatedRoleDto);

        mockMvc.perform(put("/users/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));

        verify(userService).updateRole(eq(1L), any(UserRoleUpdateRequestDto.class));
    }

    @Test
    @DisplayName("PUT /users/{id}/role: returns 400 when role is invalid/null")
    void updateRole_InvalidRole_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/users/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
