package task_management_app.dto.task;

import lombok.Data;
import task_management_app.model.Status;

@Data
public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private Long assigneeId;
}

