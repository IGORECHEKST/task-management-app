package task_management_app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import task_management_app.dto.project.ProjectCreateRequestDto;
import task_management_app.dto.project.ProjectResponseDto;
import task_management_app.dto.project.ProjectUpdateRequestDto;
import task_management_app.model.Project;
import task_management_app.service.ProjectService;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private ObjectMapper objectMapper;
    private ProjectResponseDto testResponseDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testResponseDto = new ProjectResponseDto();
        testResponseDto.setId(1L);
        testResponseDto.setName("Test Project");
        testResponseDto.setDescription("Project Description");
        testResponseDto.setStartDate(LocalDate.of(2026, 1, 1));
        testResponseDto.setEndDate(LocalDate.of(2026, 12, 31));
        testResponseDto.setStatus(Project.Status.INITIATED);

        mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("POST /api/projects: creates a new project and returns 201 Created")
    void createProject_ValidRequest_ReturnsCreated() throws Exception {
        ProjectCreateRequestDto requestDto = new ProjectCreateRequestDto();
        requestDto.setName("Test Project");
        requestDto.setDescription("Project Description");
        requestDto.setStartDate(LocalDate.of(2026, 1, 1));
        requestDto.setEndDate(LocalDate.of(2026, 12, 31));
        requestDto.setStatus(Project.Status.INITIATED);

        when(projectService.save(any(ProjectCreateRequestDto.class))).thenReturn(testResponseDto);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.status").value("INITIATED"));

        verify(projectService).save(any(ProjectCreateRequestDto.class));
    }

    @Test
    @DisplayName("POST /api/projects: returns 400 Bad Request when name is blank")
    void createProject_BlankName_ReturnsBadRequest() throws Exception {
        ProjectCreateRequestDto requestDto = new ProjectCreateRequestDto();
        requestDto.setName("");
        requestDto.setStatus(Project.Status.INITIATED);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/projects: returns list of projects")
    void getAllProjects_ReturnsProjectList() throws Exception {
        when(projectService.findAll(any(Pageable.class))).thenReturn(List.of(testResponseDto));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Project"));

        verify(projectService).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/projects?status=INITIATED&search=Test: returns filtered list of projects")
    void getAllProjects_WithFilters_ReturnsFilteredProjectList() throws Exception {
        when(projectService.findAll(
                eq(Project.Status.INITIATED),
                eq("Test"),
                any(Pageable.class)
        )).thenReturn(List.of(testResponseDto));

        mockMvc.perform(get("/api/projects")
                        .param("status", "INITIATED")
                        .param("search", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Project"));

        verify(projectService).findAll(
                eq(Project.Status.INITIATED),
                eq("Test"),
                any(Pageable.class)
        );
    }

    @Test
    @DisplayName("GET /api/projects/{id}: returns project details by ID")
    void getProjectById_ReturnsProject() throws Exception {
        when(projectService.findById(1L)).thenReturn(testResponseDto);

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.status").value("INITIATED"));

        verify(projectService).findById(1L);
    }

    @Test
    @DisplayName("PUT /api/projects/{id}: updates project and returns 200 OK")
    void updateProject_ValidRequest_ReturnsUpdatedProject() throws Exception {
        ProjectUpdateRequestDto requestDto = new ProjectUpdateRequestDto();
        requestDto.setName("Updated Project");
        requestDto.setDescription("Updated Description");
        requestDto.setStartDate(LocalDate.of(2026, 2, 1));
        requestDto.setEndDate(LocalDate.of(2026, 11, 30));
        requestDto.setStatus(Project.Status.IN_PROGRESS);

        ProjectResponseDto updatedDto = new ProjectResponseDto();
        updatedDto.setId(1L);
        updatedDto.setName("Updated Project");
        updatedDto.setDescription("Updated Description");
        updatedDto.setStatus(Project.Status.IN_PROGRESS);

        when(projectService.update(eq(1L), any(ProjectUpdateRequestDto.class))).thenReturn(updatedDto);

        mockMvc.perform(put("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Project"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        verify(projectService).update(eq(1L), any(ProjectUpdateRequestDto.class));
    }

    @Test
    @DisplayName("DELETE /api/projects/{id}: deletes project and returns 204 No Content")
    void deleteProject_ReturnsNoContent() throws Exception {
        doNothing().when(projectService).deleteById(1L);

        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isNoContent());

        verify(projectService).deleteById(1L);
    }
}

