package task_management_app.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("this-is-a-very-secure-and-long-secret-key-for-our-task-management-app");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L);
    }

    @Test
    @DisplayName("generateToken & getUsername: generates valid JWT and extracts subject")
    void generateTokenAndExtractUsername_ValidToken_Success() {
        String token = jwtUtil.generateToken("john_doe");

        assertNotNull(token);
        assertTrue(jwtUtil.isValidToken(token));
        assertEquals("john_doe", jwtUtil.getUsername(token));
    }

    @Test
    @DisplayName("isValidToken: returns false for malformed token")
    void isValidToken_InvalidToken_ReturnsFalse() {
        assertFalse(jwtUtil.isValidToken("invalid.jwt.token"));
    }
}

