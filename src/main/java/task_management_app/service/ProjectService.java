package task_management_app.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import task_management_app.dto.project.ProjectCreateRequestDto;
import task_management_app.dto.project.ProjectResponseDto;
import task_management_app.dto.project.ProjectUpdateRequestDto;

public interface ProjectService {
    ProjectResponseDto save(ProjectCreateRequestDto requestDto);

    List<ProjectResponseDto> findAll(Pageable pageable);

    ProjectResponseDto findById(Long id);

    ProjectResponseDto update(Long id, ProjectUpdateRequestDto requestDto);

    void deleteById(Long id);
}

