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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import task_management_app.dto.project.ProjectCreateRequestDto;
import task_management_app.dto.project.ProjectResponseDto;
import task_management_app.dto.project.ProjectUpdateRequestDto;
import task_management_app.model.Project;
import task_management_app.service.ProjectService;

@Tag(name = "Project Management", description = "Endpoints for managing projects")
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new project", description = "Create a new project with the provided details")
    public ProjectResponseDto createProject(@RequestBody @Valid ProjectCreateRequestDto requestDto) {
        return projectService.save(requestDto);
    }

    @GetMapping
    @Operation(summary = "Retrieve projects", description = "Retrieve projects with optional status and search filters")
    public List<ProjectResponseDto> getAllProjects(
            @RequestParam(required = false) Project.Status status,
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {
        if (status == null && (search == null || search.isBlank())) {
            return projectService.findAll(pageable);
        }
        return projectService.findAll(status, search, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve project details", description = "Retrieve details of a project by its ID")
    public ProjectResponseDto getProjectById(@PathVariable Long id) {
        return projectService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update project", description = "Update an existing project by its ID")
    public ProjectResponseDto updateProject(
            @PathVariable Long id,
            @RequestBody @Valid ProjectUpdateRequestDto requestDto
    ) {
        return projectService.update(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete project", description = "Delete a project by its ID")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteById(id);
    }
}
