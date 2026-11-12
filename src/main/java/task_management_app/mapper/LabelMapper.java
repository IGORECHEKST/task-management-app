package task_management_app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import task_management_app.dto.label.LabelCreateRequestDto;
import task_management_app.dto.label.LabelResponseDto;
import task_management_app.dto.label.LabelUpdateRequestDto;
import task_management_app.model.Label;

@Mapper(componentModel = "spring")
public interface LabelMapper {

    LabelResponseDto toDto(Label label);

    @Mapping(target = "id", ignore = true)
    Label toModel(LabelCreateRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    void updateModelFromDto(LabelUpdateRequestDto requestDto, @MappingTarget Label label);
}

