package task_management_app.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import task_management_app.dto.task.TaskCreateRequestDto;
import task_management_app.dto.task.TaskResponseDto;
import task_management_app.exception.EntityNotFoundException;
import task_management_app.mapper.TaskMapper;
import task_management_app.model.Task;
import task_management_app.model.User;
import task_management_app.repository.TaskRepository;
import task_management_app.service.TaskService;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskResponseDto save(User user, TaskCreateRequestDto requestDto) {
        Task task = taskMapper.toModel(requestDto);
        task.setAssignee(user);
        Task savedTask = taskRepository.save(task);
        return taskMapper.toDto(savedTask);
    }

    @Override
    public List<TaskResponseDto> findAll(Pageable pageable) {
        return taskRepository.findAll(pageable).stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Override
    public TaskResponseDto findById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find task by id: " + id)
        );
        return taskMapper.toDto(task);
    }
}
