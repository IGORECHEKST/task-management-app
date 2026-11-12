package task_management_app.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import task_management_app.dto.label.LabelCreateRequestDto;
import task_management_app.dto.label.LabelResponseDto;
import task_management_app.dto.label.LabelUpdateRequestDto;

public interface LabelService {
    LabelResponseDto save(LabelCreateRequestDto requestDto);

    List<LabelResponseDto> findAll(Pageable pageable);

    LabelResponseDto findById(Long id);

    LabelResponseDto update(Long id, LabelUpdateRequestDto requestDto);

    void deleteById(Long id);
}

