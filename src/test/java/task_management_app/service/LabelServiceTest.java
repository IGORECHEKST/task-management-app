package task_management_app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import task_management_app.dto.label.LabelCreateRequestDto;
import task_management_app.dto.label.LabelResponseDto;
import task_management_app.dto.label.LabelUpdateRequestDto;
import task_management_app.exception.EntityNotFoundException;
import task_management_app.mapper.LabelMapper;
import task_management_app.model.Label;
import task_management_app.repository.LabelRepository;
import task_management_app.service.impl.LabelServiceImpl;

@ExtendWith(MockitoExtension.class)
class LabelServiceTest {

    @Mock
    private LabelRepository labelRepository;

    @Mock
    private LabelMapper labelMapper;

    @InjectMocks
    private LabelServiceImpl labelService;

    private Label label;
    private LabelResponseDto labelResponseDto;
    private LabelCreateRequestDto createRequestDto;
    private LabelUpdateRequestDto updateRequestDto;

    @BeforeEach
    void setUp() {
        label = new Label();
        label.setId(1L);
        label.setName("Bug");
        label.setColor("#FF0000");

        labelResponseDto = new LabelResponseDto();
        labelResponseDto.setId(1L);
        labelResponseDto.setName("Bug");
        labelResponseDto.setColor("#FF0000");

        createRequestDto = new LabelCreateRequestDto();
        createRequestDto.setName("Bug");
        createRequestDto.setColor("#FF0000");

        updateRequestDto = new LabelUpdateRequestDto();
        updateRequestDto.setName("Critical Bug");
        updateRequestDto.setColor("#8B0000");
    }

    @Test
    @DisplayName("save: successfully saves and returns a new label")
    void save_ValidRequest_Success() {
        when(labelMapper.toModel(createRequestDto)).thenReturn(label);
        when(labelRepository.save(label)).thenReturn(label);
        when(labelMapper.toDto(label)).thenReturn(labelResponseDto);

        LabelResponseDto result = labelService.save(createRequestDto);

        assertNotNull(result);
        assertEquals(labelResponseDto.getId(), result.getId());
        assertEquals(labelResponseDto.getName(), result.getName());
        assertEquals(labelResponseDto.getColor(), result.getColor());
        verify(labelRepository).save(label);
    }

    @Test
    @DisplayName("findAll: returns a paginated list of labels")
    void findAll_ValidPageable_ReturnsLabelList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Label> labelPage = new PageImpl<>(List.of(label));

        when(labelRepository.findAll(pageable)).thenReturn(labelPage);
        when(labelMapper.toDto(label)).thenReturn(labelResponseDto);

        List<LabelResponseDto> result = labelService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(labelResponseDto.getName(), result.get(0).getName());
        verify(labelRepository).findAll(pageable);
    }

    @Test
    @DisplayName("findById: returns label when found")
    void findById_ExistingId_ReturnsLabel() {
        when(labelRepository.findById(1L)).thenReturn(Optional.of(label));
        when(labelMapper.toDto(label)).thenReturn(labelResponseDto);

        LabelResponseDto result = labelService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Bug", result.getName());
        assertEquals("#FF0000", result.getColor());
    }

    @Test
    @DisplayName("findById: throws EntityNotFoundException when label not found")
    void findById_NonExistingId_ThrowsEntityNotFoundException() {
        when(labelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> labelService.findById(999L));
    }

    @Test
    @DisplayName("update: successfully updates existing label")
    void update_ExistingId_Success() {
        LabelResponseDto updatedResponseDto = new LabelResponseDto();
        updatedResponseDto.setId(1L);
        updatedResponseDto.setName("Critical Bug");
        updatedResponseDto.setColor("#8B0000");

        when(labelRepository.findById(1L)).thenReturn(Optional.of(label));
        when(labelRepository.save(label)).thenReturn(label);
        when(labelMapper.toDto(label)).thenReturn(updatedResponseDto);

        LabelResponseDto result = labelService.update(1L, updateRequestDto);

        assertNotNull(result);
        assertEquals("Critical Bug", result.getName());
        assertEquals("#8B0000", result.getColor());
        verify(labelMapper).updateModelFromDto(updateRequestDto, label);
        verify(labelRepository).save(label);
    }

    @Test
    @DisplayName("update: throws EntityNotFoundException when label does not exist")
    void update_NonExistingId_ThrowsEntityNotFoundException() {
        when(labelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> labelService.update(999L, updateRequestDto));
    }

    @Test
    @DisplayName("deleteById: successfully deletes existing label")
    void deleteById_ExistingId_Success() {
        when(labelRepository.existsById(1L)).thenReturn(true);

        labelService.deleteById(1L);

        verify(labelRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteById: throws EntityNotFoundException when label does not exist")
    void deleteById_NonExistingId_ThrowsEntityNotFoundException() {
        when(labelRepository.existsById(999L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> labelService.deleteById(999L));
    }
}

