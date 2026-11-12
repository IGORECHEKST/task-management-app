package task_management_app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
import task_management_app.dto.project.ProjectCreateRequestDto;
import task_management_app.dto.project.ProjectResponseDto;
import task_management_app.dto.project.ProjectUpdateRequestDto;
import task_management_app.exception.EntityNotFoundException;
import task_management_app.mapper.ProjectMapper;
import task_management_app.model.Project;
import task_management_app.repository.ProjectRepository;
import task_management_app.service.impl.ProjectServiceImpl;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project project;
    private ProjectResponseDto projectResponseDto;
    private ProjectCreateRequestDto createRequestDto;
    private ProjectUpdateRequestDto updateRequestDto;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setDescription("Project Description");
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setEndDate(LocalDate.of(2026, 12, 31));
        project.setStatus(Project.Status.INITIATED);

        projectResponseDto = new ProjectResponseDto();
        projectResponseDto.setId(1L);
        projectResponseDto.setName("Test Project");
        projectResponseDto.setDescription("Project Description");
        projectResponseDto.setStartDate(LocalDate.of(2026, 1, 1));
        projectResponseDto.setEndDate(LocalDate.of(2026, 12, 31));
        projectResponseDto.setStatus(Project.Status.INITIATED);

        createRequestDto = new ProjectCreateRequestDto();
        createRequestDto.setName("Test Project");
        createRequestDto.setDescription("Project Description");
        createRequestDto.setStartDate(LocalDate.of(2026, 1, 1));
        createRequestDto.setEndDate(LocalDate.of(2026, 12, 31));
        createRequestDto.setStatus(Project.Status.INITIATED);

        updateRequestDto = new ProjectUpdateRequestDto();
        updateRequestDto.setName("Updated Project");
        updateRequestDto.setDescription("Updated Description");
        updateRequestDto.setStartDate(LocalDate.of(2026, 2, 1));
        updateRequestDto.setEndDate(LocalDate.of(2026, 11, 30));
        updateRequestDto.setStatus(Project.Status.IN_PROGRESS);
    }

    @Test
    @DisplayName("save: successfully saves and returns a new project")
    void save_ValidRequest_Success() {
        when(projectMapper.toModel(createRequestDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectResponseDto);

        ProjectResponseDto result = projectService.save(createRequestDto);

        assertNotNull(result);
        assertEquals(projectResponseDto.getId(), result.getId());
        assertEquals(projectResponseDto.getName(), result.getName());
        assertEquals(projectResponseDto.getStatus(), result.getStatus());
        verify(projectRepository).save(project);
    }

    @Test
    @DisplayName("findAll: returns a paginated list of projects")
    void findAll_ValidPageable_ReturnsProjectList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> projectPage = new PageImpl<>(List.of(project));

        when(projectRepository.findAll(pageable)).thenReturn(projectPage);
        when(projectMapper.toDto(project)).thenReturn(projectResponseDto);

        List<ProjectResponseDto> result = projectService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(projectResponseDto.getName(), result.get(0).getName());
        verify(projectRepository).findAll(pageable);
    }

    @Test
    @DisplayName("findAll with filters: returns projects filtered by status and search")
    void findAllWithFilters_StatusAndSearch_ReturnsFilteredProjects() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> projectPage = new PageImpl<>(List.of(project));

        when(projectRepository.findAllWithFilters(Project.Status.INITIATED, "Test", pageable))
                .thenReturn(projectPage);
        when(projectMapper.toDto(project)).thenReturn(projectResponseDto);

        List<ProjectResponseDto> result = projectService.findAll(
                Project.Status.INITIATED,
                "Test",
                pageable
        );

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Project", result.get(0).getName());
        verify(projectRepository).findAllWithFilters(Project.Status.INITIATED, "Test", pageable);
    }

    @Test
    @DisplayName("findById: returns project when found")
    void findById_ExistingId_ReturnsProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMapper.toDto(project)).thenReturn(projectResponseDto);

        ProjectResponseDto result = projectService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Project", result.getName());
    }

    @Test
    @DisplayName("findById: throws EntityNotFoundException when project not found")
    void findById_NonExistingId_ThrowsEntityNotFoundException() {
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> projectService.findById(999L));
    }

    @Test
    @DisplayName("update: successfully updates existing project")
    void update_ExistingId_Success() {
        ProjectResponseDto updatedResponseDto = new ProjectResponseDto();
        updatedResponseDto.setId(1L);
        updatedResponseDto.setName("Updated Project");
        updatedResponseDto.setDescription("Updated Description");
        updatedResponseDto.setStatus(Project.Status.IN_PROGRESS);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(updatedResponseDto);

        ProjectResponseDto result = projectService.update(1L, updateRequestDto);

        assertNotNull(result);
        assertEquals("Updated Project", result.getName());
        assertEquals(Project.Status.IN_PROGRESS, result.getStatus());
        verify(projectMapper).updateModelFromDto(updateRequestDto, project);
        verify(projectRepository).save(project);
    }

    @Test
    @DisplayName("update: throws EntityNotFoundException when project does not exist")
    void update_NonExistingId_ThrowsEntityNotFoundException() {
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> projectService.update(999L, updateRequestDto));
    }

    @Test
    @DisplayName("deleteById: successfully deletes existing project")
    void deleteById_ExistingId_Success() {
        when(projectRepository.existsById(1L)).thenReturn(true);

        projectService.deleteById(1L);

        verify(projectRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteById: throws EntityNotFoundException when project does not exist")
    void deleteById_NonExistingId_ThrowsEntityNotFoundException() {
        when(projectRepository.existsById(999L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> projectService.deleteById(999L));
    }
}

