package task_management_app.controller;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import task_management_app.dto.task.TaskCreateRequestDto;
import task_management_app.dto.task.TaskResponseDto;
import task_management_app.dto.task.TaskUpdateRequestDto;
import task_management_app.model.User;
import task_management_app.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDto createTask(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid TaskCreateRequestDto requestDto
    ) {
        return taskService.save(user, requestDto);
    }

    @GetMapping
    public List<TaskResponseDto> getAllTasks(Pageable pageable) {
        return taskService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public TaskResponseDto getTaskById(@PathVariable Long id) {
        return taskService.findById(id);
    }

    @PutMapping("/{id}")
    public TaskResponseDto updateTask(
            @PathVariable Long id,
            @RequestBody @Valid TaskUpdateRequestDto requestDto
    ) {
        return taskService.update(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteById(id);
    }
}
