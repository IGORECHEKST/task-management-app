package task_management_app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import task_management_app.dto.task.TaskCreateRequestDto;
import task_management_app.dto.task.TaskResponseDto;
import task_management_app.dto.task.TaskUpdateRequestDto;
import task_management_app.model.Task;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(source = "assignee.id", target = "assigneeId")
    TaskResponseDto toDto(Task task);

    Task toModel(TaskCreateRequestDto requestDto);

    void updateModelFromDto(TaskUpdateRequestDto requestDto, @MappingTarget Task task);
}
