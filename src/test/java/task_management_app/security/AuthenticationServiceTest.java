package task_management_app.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import task_management_app.dto.user.UserLoginRequestDto;
import task_management_app.dto.user.UserLoginResponseDto;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("authenticate: authenticates credentials and generates JWT token")
    void authenticate_ValidRequest_ReturnsLoginResponse() {
        UserLoginRequestDto requestDto = new UserLoginRequestDto();
        requestDto.setEmail("john@example.com");
        requestDto.setPassword("password123");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("john_doe");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken("john_doe")).thenReturn("mocked-jwt-token");

        UserLoginResponseDto response = authenticationService.authenticate(requestDto);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.token());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken("john_doe");
    }
}

