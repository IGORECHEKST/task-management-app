package task_management_app.dto.label;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LabelCreateRequestDto {
    @NotBlank
    private String name;

    @NotBlank
    private String color;
}

