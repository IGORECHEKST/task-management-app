package task_management_app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import task_management_app.dto.task.TaskCreateRequestDto;
import task_management_app.dto.task.TaskResponseDto;
import task_management_app.dto.task.TaskUpdateRequestDto;
import task_management_app.model.Priority;
import task_management_app.model.Status;
import task_management_app.model.User;
import task_management_app.service.TaskService;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private ObjectMapper objectMapper;
    private User testUser;
    private TaskResponseDto testResponseDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testResponseDto = new TaskResponseDto();
        testResponseDto.setId(1L);
        testResponseDto.setName("Implement API");
        testResponseDto.setDescription("Task description");
        testResponseDto.setPriority(Priority.HIGH);
        testResponseDto.setStatus(Status.IN_PROGRESS);
        testResponseDto.setDueDate(LocalDate.of(2026, 12, 31));
        testResponseDto.setProjectId(10L);
        testResponseDto.setAssigneeId(1L);
        testResponseDto.setLabelIds(Set.of(100L));

        HandlerMethodArgumentResolver authPrincipalResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
            }

            @Override
            public Object resolveArgument(
                    MethodParameter parameter,
                    ModelAndViewContainer mavContainer,
                    NativeWebRequest webRequest,
                    WebDataBinderFactory binderFactory
            ) {
                return testUser;
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setCustomArgumentResolvers(
                        authPrincipalResolver,
                        new PageableHandlerMethodArgumentResolver()
                )
                .build();
    }

    @Test
    @DisplayName("POST /api/tasks: creates a new task and returns 201 Created")
    void createTask_ValidRequest_ReturnsCreated() throws Exception {
        TaskCreateRequestDto requestDto = new TaskCreateRequestDto();
        requestDto.setName("Implement API");
        requestDto.setDescription("Task description");
        requestDto.setPriority(Priority.HIGH);
        requestDto.setStatus(Status.IN_PROGRESS);
        requestDto.setDueDate(LocalDate.of(2026, 12, 31));
        requestDto.setProjectId(10L);
        requestDto.setAssigneeId(1L);
        requestDto.setLabelIds(Set.of(100L));

        when(taskService.save(any(User.class), any(TaskCreateRequestDto.class)))
                .thenReturn(testResponseDto);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Implement API"))
                .andExpect(jsonPath("$.projectId").value(10L))
                .andExpect(jsonPath("$.labelIds[0]").value(100L));

        verify(taskService).save(any(User.class), any(TaskCreateRequestDto.class));
    }

    @Test
    @DisplayName("POST /api/tasks: returns 400 Bad Request when projectId is missing")
    void createTask_MissingProjectId_ReturnsBadRequest() throws Exception {
        TaskCreateRequestDto requestDto = new TaskCreateRequestDto();
        requestDto.setName("Implement API");
        requestDto.setPriority(Priority.HIGH);
        requestDto.setStatus(Status.IN_PROGRESS);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/tasks: returns all tasks when projectId is not provided")
    void getAllTasks_WithoutProjectId_ReturnsTaskList() throws Exception {
        when(taskService.findAll(isNull(), any(Pageable.class)))
                .thenReturn(List.of(testResponseDto));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Implement API"));

        verify(taskService).findAll(isNull(), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/tasks?projectId=10: returns tasks filtered by projectId")
    void getAllTasks_WithProjectId_ReturnsFilteredTaskList() throws Exception {
        when(taskService.findAll(eq(10L), any(Pageable.class)))
                .thenReturn(List.of(testResponseDto));

        mockMvc.perform(get("/api/tasks").param("projectId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].projectId").value(10L));

        verify(taskService).findAll(eq(10L), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/tasks/{id}: returns task details by ID")
    void getTaskById_ReturnsTask() throws Exception {
        when(taskService.findById(1L)).thenReturn(testResponseDto);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Implement API"));

        verify(taskService).findById(1L);
    }

    @Test
    @DisplayName("PUT /api/tasks/{id}: updates task and returns 200 OK")
    void updateTask_ValidRequest_ReturnsUpdatedTask() throws Exception {
        TaskUpdateRequestDto requestDto = new TaskUpdateRequestDto();
        requestDto.setName("Updated Task");
        requestDto.setDescription("Updated description");
        requestDto.setPriority(Priority.MEDIUM);
        requestDto.setStatus(Status.COMPLETED);
        requestDto.setProjectId(10L);
        requestDto.setAssigneeId(1L);
        requestDto.setLabelIds(Set.of(100L));

        when(taskService.update(eq(1L), any(TaskUpdateRequestDto.class)))
                .thenReturn(testResponseDto);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(taskService).update(eq(1L), any(TaskUpdateRequestDto.class));
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id}: deletes task and returns 204 No Content")
    void deleteTask_ReturnsNoContent() throws Exception {
        doNothing().when(taskService).deleteById(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService).deleteById(1L);
    }
}

