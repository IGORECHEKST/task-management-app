package task_management_app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import task_management_app.dto.task.TaskCreateRequestDto;
import task_management_app.dto.task.TaskResponseDto;
import task_management_app.dto.task.TaskUpdateRequestDto;
import task_management_app.model.Priority;
import task_management_app.model.Status;
import task_management_app.model.User;
import task_management_app.service.TaskService;

@Tag(name = "Task Management", description = "Endpoints for managing tasks within projects")
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new task", description = "Create a new task within a project")
    public TaskResponseDto createTask(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid TaskCreateRequestDto requestDto
    ) {
        return taskService.save(user, requestDto);
    }

    @GetMapping
    @Operation(summary = "Retrieve tasks", description = "Retrieve tasks with optional search and filters")
    public List<TaskResponseDto> getAllTasks(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {
        if (status == null && priority == null && assigneeId == null
                && (search == null || search.isBlank())) {
            return taskService.findAll(projectId, pageable);
        }
        return taskService.findAll(projectId, status, priority, assigneeId, search, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve task details", description = "Retrieve details of a task by its ID")
    public TaskResponseDto getTaskById(@PathVariable Long id) {
        return taskService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task", description = "Update an existing task by its ID")
    public TaskResponseDto updateTask(
            @PathVariable Long id,
            @RequestBody @Valid TaskUpdateRequestDto requestDto
    ) {
        return taskService.update(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete task", description = "Delete a task by its ID")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteById(id);
    }
}
