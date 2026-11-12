package task_management_app.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("handleEntityNotFoundException: returns 404 NOT_FOUND")
    void handleEntityNotFoundException_ReturnsNotFound() {
        EntityNotFoundException ex = new EntityNotFoundException("Entity not found");

        ResponseEntity<Object> response = exceptionHandler.handleEntityNotFoundException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("handleRegistrationException: returns 409 CONFLICT")
    void handleRegistrationException_ReturnsConflict() {
        RegistrationException ex = new RegistrationException("User already exists");

        ResponseEntity<Object> response = exceptionHandler.handleRegistrationException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    @DisplayName("handleFileStorageException: returns 502 BAD_GATEWAY")
    void handleFileStorageException_ReturnsBadGateway() {
        FileStorageException ex = new FileStorageException("Dropbox unavailable");

        ResponseEntity<Object> response = exceptionHandler.handleFileStorageException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
    }
}

