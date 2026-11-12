package task_management_app.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import task_management_app.dto.task.TaskCreateRequestDto;
import task_management_app.dto.task.TaskResponseDto;
import task_management_app.dto.task.TaskUpdateRequestDto;
import task_management_app.model.Priority;
import task_management_app.model.Status;
import task_management_app.model.User;

public interface TaskService {
    TaskResponseDto save(User user, TaskCreateRequestDto requestDto);

    List<TaskResponseDto> findAll(Long projectId, Pageable pageable);

    List<TaskResponseDto> findAll(
            Long projectId,
            Status status,
            Priority priority,
            Long assigneeId,
            String search,
            Pageable pageable
    );

    TaskResponseDto findById(Long id);

    TaskResponseDto update(Long id, TaskUpdateRequestDto requestDto);

    void deleteById(Long id);
}
