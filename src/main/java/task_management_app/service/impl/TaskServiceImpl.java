package task_management_app.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task_management_app.dto.task.TaskCreateRequestDto;
import task_management_app.dto.task.TaskResponseDto;
import task_management_app.dto.task.TaskUpdateRequestDto;
import task_management_app.exception.EntityNotFoundException;
import task_management_app.mapper.TaskMapper;
import task_management_app.model.Task;
import task_management_app.model.User;
import task_management_app.repository.TaskRepository;
import task_management_app.repository.UserRepository;
import task_management_app.service.TaskService;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskResponseDto save(User user, TaskCreateRequestDto requestDto) {
        Task task = taskMapper.toModel(requestDto);
        if (requestDto.getAssigneeId() != null) {
            User assignee = userRepository.findById(requestDto.getAssigneeId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Can't find user by id: " + requestDto.getAssigneeId()));
            task.setAssignee(assignee);
        } else {
            task.setAssignee(user);
        }
        Task savedTask = taskRepository.save(task);
        return taskMapper.toDto(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDto> findAll(Pageable pageable) {
        return taskRepository.findAll(pageable).stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDto findById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find task by id: " + id)
        );
        return taskMapper.toDto(task);
    }

    @Override
    public TaskResponseDto update(Long id, TaskUpdateRequestDto requestDto) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find task by id: " + id)
        );
        taskMapper.updateModelFromDto(requestDto, task);
        if (requestDto.getAssigneeId() != null) {
            User assignee = userRepository.findById(requestDto.getAssigneeId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Can't find user by id: " + requestDto.getAssigneeId()));
            task.setAssignee(assignee);
        }
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public void deleteById(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Can't find task by id: " + id);
        }
        taskRepository.deleteById(id);
    }
}
