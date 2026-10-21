package task_management_app.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import task_management_app.dto.task.TaskCreateRequestDto;
import task_management_app.dto.task.TaskResponseDto;
import task_management_app.model.User;

public interface TaskService {
    TaskResponseDto save(User user, TaskCreateRequestDto requestDto);

    List<TaskResponseDto> findAll(Pageable pageable);

    TaskResponseDto findById(Long id);
}
