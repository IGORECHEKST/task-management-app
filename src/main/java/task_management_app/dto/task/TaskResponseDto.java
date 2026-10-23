package task_management_app.dto.task;

import java.time.LocalDate;
import lombok.Data;
import task_management_app.model.Priority;
import task_management_app.model.Status;

@Data
public class TaskResponseDto {
    private Long id;
    private String name;
    private String description;
    private Priority priority;
    private Status status;
    private LocalDate dueDate;
    private Long assigneeId;
}
