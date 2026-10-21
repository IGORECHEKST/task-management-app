package task_management_app.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import task_management_app.model.Status;

@Data
public class TaskUpdateRequestDto {
    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Status status;
}
