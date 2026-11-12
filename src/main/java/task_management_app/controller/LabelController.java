package task_management_app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import task_management_app.dto.label.LabelCreateRequestDto;
import task_management_app.dto.label.LabelResponseDto;
import task_management_app.dto.label.LabelUpdateRequestDto;
import task_management_app.service.LabelService;

@Tag(name = "Label Management", description = "Endpoints for managing labels")
@RestController
@RequestMapping("/api/labels")
@RequiredArgsConstructor
public class LabelController {

    private final LabelService labelService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new label", description = "Create a new label with name and color")
    public LabelResponseDto createLabel(@RequestBody @Valid LabelCreateRequestDto requestDto) {
        return labelService.save(requestDto);
    }

    @GetMapping
    @Operation(summary = "Retrieve labels", description = "Retrieve a paginated list of labels")
    public List<LabelResponseDto> getAllLabels(Pageable pageable) {
        return labelService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve label details", description = "Retrieve details of a label by its ID")
    public LabelResponseDto getLabelById(@PathVariable Long id) {
        return labelService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update label", description = "Update an existing label by its ID")
    public LabelResponseDto updateLabel(
            @PathVariable Long id,
            @RequestBody @Valid LabelUpdateRequestDto requestDto
    ) {
        return labelService.update(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete label", description = "Delete a label by its ID")
    public void deleteLabel(@PathVariable Long id) {
        labelService.deleteById(id);
    }
}

