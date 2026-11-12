package task_management_app.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;
import task_management_app.model.Project;

@Data
public class ProjectCreateRequestDto {
    @NotBlank
    private String name;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull
    private Project.Status status;
}

