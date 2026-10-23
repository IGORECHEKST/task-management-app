package task_management_app.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;
import task_management_app.model.Priority;
import task_management_app.model.Status;

@Data
public class TaskCreateRequestDto {
    @NotBlank
    private String name;

    private String description;

    @NotNull
    private Priority priority;

    @NotNull
    private Status status;

    private LocalDate dueDate;

    private Long assigneeId;
}
