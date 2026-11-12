package task_management_app.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task_management_app.dto.task.TaskCreateRequestDto;
import task_management_app.dto.task.TaskResponseDto;
import task_management_app.dto.task.TaskUpdateRequestDto;
import task_management_app.exception.EntityNotFoundException;
import task_management_app.mapper.TaskMapper;
import task_management_app.model.Label;
import task_management_app.model.Project;
import task_management_app.model.Task;
import task_management_app.model.User;
import task_management_app.repository.LabelRepository;
import task_management_app.repository.ProjectRepository;
import task_management_app.repository.TaskRepository;
import task_management_app.repository.UserRepository;
import task_management_app.service.NotificationService;
import task_management_app.service.TaskService;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final LabelRepository labelRepository;
    private final TaskMapper taskMapper;
    private final NotificationService notificationService;

    @Override
    public TaskResponseDto save(User user, TaskCreateRequestDto requestDto) {
        Task task = taskMapper.toModel(requestDto);

        Project project = projectRepository.findById(requestDto.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find project by id: " + requestDto.getProjectId()));
        task.setProject(project);

        if (requestDto.getAssigneeId() != null) {
            User assignee = userRepository.findById(requestDto.getAssigneeId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Can't find user by id: " + requestDto.getAssigneeId()));
            task.setAssignee(assignee);
        } else {
            task.setAssignee(user);
        }

        if (requestDto.getLabelIds() != null) {
            task.setLabels(resolveLabels(requestDto.getLabelIds()));
        }

        Task savedTask = taskRepository.save(task);
        notificationService.notifyTaskAssigned(savedTask.getAssignee(), savedTask);
        return taskMapper.toDto(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDto> findAll(Long projectId, Pageable pageable) {
        Page<Task> tasks = projectId != null
                ? taskRepository.findAllByProjectId(projectId, pageable)
                : taskRepository.findAll(pageable);
        return tasks.stream()
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

        Project project = projectRepository.findById(requestDto.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find project by id: " + requestDto.getProjectId()));
        task.setProject(project);

        if (requestDto.getAssigneeId() != null) {
            User assignee = userRepository.findById(requestDto.getAssigneeId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Can't find user by id: " + requestDto.getAssigneeId()));
            task.setAssignee(assignee);
        }

        if (requestDto.getLabelIds() != null) {
            task.setLabels(resolveLabels(requestDto.getLabelIds()));
        }

        Task savedTask = taskRepository.save(task);
        if (savedTask.getAssignee() != null) {
            notificationService.notifyTaskAssigned(savedTask.getAssignee(), savedTask);
        }
        return taskMapper.toDto(savedTask);
    }

    @Override
    public void deleteById(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Can't find task by id: " + id);
        }
        taskRepository.deleteById(id);
    }

    private Set<Label> resolveLabels(Set<Long> labelIds) {
        Set<Label> labels = new HashSet<>();
        for (Long labelId : labelIds) {
            Label label = labelRepository.findById(labelId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Can't find label by id: " + labelId));
            labels.add(label);
        }
        return labels;
    }
}
