package task_management_app.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task_management_app.dto.project.ProjectCreateRequestDto;
import task_management_app.dto.project.ProjectResponseDto;
import task_management_app.dto.project.ProjectUpdateRequestDto;
import task_management_app.exception.EntityNotFoundException;
import task_management_app.mapper.ProjectMapper;
import task_management_app.model.Project;
import task_management_app.repository.ProjectRepository;
import task_management_app.service.ProjectService;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectResponseDto save(ProjectCreateRequestDto requestDto) {
        Project project = projectMapper.toModel(requestDto);
        Project savedProject = projectRepository.save(project);
        return projectMapper.toDto(savedProject);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDto> findAll(Pageable pageable) {
        return projectRepository.findAll(pageable).stream()
                .map(projectMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDto> findAll(Project.Status status, String search, Pageable pageable) {
        String normalizedSearch = (search != null && !search.isBlank()) ? search.trim() : null;
        if (status == null && normalizedSearch == null) {
            return findAll(pageable);
        }
        Page<Project> projects = projectRepository.findAllWithFilters(status, normalizedSearch, pageable);
        return projects.stream()
                .map(projectMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDto findById(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find project by id: " + id)
        );
        return projectMapper.toDto(project);
    }

    @Override
    public ProjectResponseDto update(Long id, ProjectUpdateRequestDto requestDto) {
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find project by id: " + id)
        );
        projectMapper.updateModelFromDto(requestDto, project);
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Override
    public void deleteById(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundException("Can't find project by id: " + id);
        }
        projectRepository.deleteById(id);
    }
}
