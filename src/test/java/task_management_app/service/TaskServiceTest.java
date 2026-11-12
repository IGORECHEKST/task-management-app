package task_management_app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import task_management_app.dto.task.TaskCreateRequestDto;
import task_management_app.dto.task.TaskResponseDto;
import task_management_app.dto.task.TaskUpdateRequestDto;
import task_management_app.exception.EntityNotFoundException;
import task_management_app.mapper.TaskMapper;
import task_management_app.model.Label;
import task_management_app.model.Priority;
import task_management_app.model.Project;
import task_management_app.model.Status;
import task_management_app.model.Task;
import task_management_app.model.User;
import task_management_app.repository.LabelRepository;
import task_management_app.repository.ProjectRepository;
import task_management_app.repository.TaskRepository;
import task_management_app.repository.UserRepository;
import task_management_app.service.impl.TaskServiceImpl;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private LabelRepository labelRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private User user;
    private Project project;
    private Label label;
    private Task task;
    private TaskResponseDto taskResponseDto;
    private TaskCreateRequestDto createRequestDto;
    private TaskUpdateRequestDto updateRequestDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        project = new Project();
        project.setId(10L);
        project.setName("Main Project");
        project.setStatus(Project.Status.IN_PROGRESS);

        label = new Label();
        label.setId(100L);
        label.setName("Backend");
        label.setColor("#00FF00");

        task = new Task();
        task.setId(1L);
        task.setName("Implement API");
        task.setDescription("Task description");
        task.setPriority(Priority.HIGH);
        task.setStatus(Status.IN_PROGRESS);
        task.setDueDate(LocalDate.of(2026, 12, 31));
        task.setProject(project);
        task.setAssignee(user);
        task.setLabels(Set.of(label));

        taskResponseDto = new TaskResponseDto();
        taskResponseDto.setId(1L);
        taskResponseDto.setName("Implement API");
        taskResponseDto.setDescription("Task description");
        taskResponseDto.setPriority(Priority.HIGH);
        taskResponseDto.setStatus(Status.IN_PROGRESS);
        taskResponseDto.setDueDate(LocalDate.of(2026, 12, 31));
        taskResponseDto.setProjectId(10L);
        taskResponseDto.setAssigneeId(1L);
        taskResponseDto.setLabelIds(Set.of(100L));

        createRequestDto = new TaskCreateRequestDto();
        createRequestDto.setName("Implement API");
        createRequestDto.setDescription("Task description");
        createRequestDto.setPriority(Priority.HIGH);
        createRequestDto.setStatus(Status.IN_PROGRESS);
        createRequestDto.setDueDate(LocalDate.of(2026, 12, 31));
        createRequestDto.setProjectId(10L);
        createRequestDto.setAssigneeId(1L);
        createRequestDto.setLabelIds(Set.of(100L));

        updateRequestDto = new TaskUpdateRequestDto();
        updateRequestDto.setName("Updated Task");
        updateRequestDto.setDescription("Updated description");
        updateRequestDto.setPriority(Priority.MEDIUM);
        updateRequestDto.setStatus(Status.COMPLETED);
        updateRequestDto.setDueDate(LocalDate.of(2026, 12, 15));
        updateRequestDto.setProjectId(10L);
        updateRequestDto.setAssigneeId(1L);
        updateRequestDto.setLabelIds(Set.of(100L));
    }

    @Test
    @DisplayName("save: successfully saves task with project, assignee and labels")
    void save_ValidRequest_Success() {
        when(taskMapper.toModel(createRequestDto)).thenReturn(task);
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(labelRepository.findById(100L)).thenReturn(Optional.of(label));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskResponseDto);

        TaskResponseDto result = taskService.save(user, createRequestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(10L, result.getProjectId());
        assertTrue(result.getLabelIds().contains(100L));
        verify(taskRepository).save(task);
    }

    @Test
    @DisplayName("save: throws EntityNotFoundException when project does not exist")
    void save_ProjectNotFound_ThrowsEntityNotFoundException() {
        when(taskMapper.toModel(createRequestDto)).thenReturn(task);
        when(projectRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.save(user, createRequestDto));
    }

    @Test
    @DisplayName("save: throws EntityNotFoundException when label does not exist")
    void save_LabelNotFound_ThrowsEntityNotFoundException() {
        when(taskMapper.toModel(createRequestDto)).thenReturn(task);
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(labelRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.save(user, createRequestDto));
    }

    @Test
    @DisplayName("findAll: returns all tasks when projectId is null")
    void findAll_NullProjectId_ReturnsAllTasks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> page = new PageImpl<>(List.of(task));

        when(taskRepository.findAll(pageable)).thenReturn(page);
        when(taskMapper.toDto(task)).thenReturn(taskResponseDto);

        List<TaskResponseDto> result = taskService.findAll(null, pageable);

        assertEquals(1, result.size());
        verify(taskRepository).findAll(pageable);
    }

    @Test
    @DisplayName("findAll: returns tasks filtered by projectId when provided")
    void findAll_WithProjectId_ReturnsFilteredTasks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> page = new PageImpl<>(List.of(task));

        when(taskRepository.findAllByProjectId(10L, pageable)).thenReturn(page);
        when(taskMapper.toDto(task)).thenReturn(taskResponseDto);

        List<TaskResponseDto> result = taskService.findAll(10L, pageable);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getProjectId());
        verify(taskRepository).findAllByProjectId(10L, pageable);
    }

    @Test
    @DisplayName("findById: returns task when found")
    void findById_ExistingId_ReturnsTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(taskResponseDto);

        TaskResponseDto result = taskService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("findById: throws EntityNotFoundException when task not found")
    void findById_NonExistingId_ThrowsEntityNotFoundException() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.findById(999L));
    }

    @Test
    @DisplayName("update: successfully updates existing task")
    void update_ExistingId_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(labelRepository.findById(100L)).thenReturn(Optional.of(label));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskResponseDto);

        TaskResponseDto result = taskService.update(1L, updateRequestDto);

        assertNotNull(result);
        verify(taskMapper).updateModelFromDto(updateRequestDto, task);
        verify(taskRepository).save(task);
    }

    @Test
    @DisplayName("update: throws EntityNotFoundException when task not found")
    void update_NonExistingId_ThrowsEntityNotFoundException() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.update(999L, updateRequestDto));
    }

    @Test
    @DisplayName("deleteById: successfully deletes existing task")
    void deleteById_ExistingId_Success() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteById(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteById: throws EntityNotFoundException when task not found")
    void deleteById_NonExistingId_ThrowsEntityNotFoundException() {
        when(taskRepository.existsById(999L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> taskService.deleteById(999L));
    }
}

