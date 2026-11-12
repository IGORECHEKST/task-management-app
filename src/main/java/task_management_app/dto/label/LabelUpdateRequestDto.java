package task_management_app.dto.label;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LabelUpdateRequestDto {
    @NotBlank
    private String name;

    @NotBlank
    private String color;
}

