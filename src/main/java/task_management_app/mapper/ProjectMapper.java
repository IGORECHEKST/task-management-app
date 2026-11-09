package task_management_app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import task_management_app.dto.project.ProjectCreateRequestDto;
import task_management_app.dto.project.ProjectResponseDto;
import task_management_app.dto.project.ProjectUpdateRequestDto;
import task_management_app.model.Project;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponseDto toDto(Project project);

    @Mapping(target = "id", ignore = true)
    Project toModel(ProjectCreateRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    void updateModelFromDto(ProjectUpdateRequestDto requestDto, @MappingTarget Project project);
}

