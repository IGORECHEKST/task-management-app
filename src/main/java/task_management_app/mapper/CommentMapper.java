package task_management_app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import task_management_app.dto.comment.CommentCreateRequestDto;
import task_management_app.dto.comment.CommentResponseDto;
import task_management_app.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "user.id", target = "userId")
    CommentResponseDto toDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "task", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    Comment toModel(CommentCreateRequestDto requestDto);
}

