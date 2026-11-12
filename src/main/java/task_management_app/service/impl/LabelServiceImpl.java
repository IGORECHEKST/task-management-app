package task_management_app.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task_management_app.dto.label.LabelCreateRequestDto;
import task_management_app.dto.label.LabelResponseDto;
import task_management_app.dto.label.LabelUpdateRequestDto;
import task_management_app.exception.EntityNotFoundException;
import task_management_app.mapper.LabelMapper;
import task_management_app.model.Label;
import task_management_app.repository.LabelRepository;
import task_management_app.service.LabelService;

@Service
@RequiredArgsConstructor
@Transactional
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Override
    public LabelResponseDto save(LabelCreateRequestDto requestDto) {
        Label label = labelMapper.toModel(requestDto);
        Label savedLabel = labelRepository.save(label);
        return labelMapper.toDto(savedLabel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LabelResponseDto> findAll(Pageable pageable) {
        return labelRepository.findAll(pageable).stream()
                .map(labelMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LabelResponseDto findById(Long id) {
        Label label = labelRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find label by id: " + id)
        );
        return labelMapper.toDto(label);
    }

    @Override
    public LabelResponseDto update(Long id, LabelUpdateRequestDto requestDto) {
        Label label = labelRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find label by id: " + id)
        );
        labelMapper.updateModelFromDto(requestDto, label);
        return labelMapper.toDto(labelRepository.save(label));
    }

    @Override
    public void deleteById(Long id) {
        if (!labelRepository.existsById(id)) {
            throw new EntityNotFoundException("Can't find label by id: " + id);
        }
        labelRepository.deleteById(id);
    }
}

