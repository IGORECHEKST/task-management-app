package task_management_app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import task_management_app.dto.attachment.AttachmentResponseDto;
import task_management_app.model.Attachment;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {

    @Mapping(source = "task.id", target = "taskId")
    AttachmentResponseDto toDto(Attachment attachment);
}

