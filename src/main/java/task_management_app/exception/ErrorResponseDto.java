package task_management_app.exception;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ErrorResponseDto {
    private LocalDateTime timestamp;
    private HttpStatus status;
    private Object errors;
}