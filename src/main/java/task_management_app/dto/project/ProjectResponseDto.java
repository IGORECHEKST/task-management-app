package task_management_app.dto.project;

import java.time.LocalDate;
import lombok.Data;
import task_management_app.model.Project;

@Data
public class ProjectResponseDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Project.Status status;
}

