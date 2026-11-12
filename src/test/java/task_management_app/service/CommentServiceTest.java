package task_management_app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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
import task_management_app.dto.comment.CommentCreateRequestDto;
import task_management_app.dto.comment.CommentResponseDto;
import task_management_app.exception.EntityNotFoundException;
import task_management_app.mapper.CommentMapper;
import task_management_app.model.Comment;
import task_management_app.model.Task;
import task_management_app.model.User;
import task_management_app.repository.CommentRepository;
import task_management_app.repository.TaskRepository;
import task_management_app.service.impl.CommentServiceImpl;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private Task task;
    private Comment comment;
    private CommentResponseDto commentResponseDto;
    private CommentCreateRequestDto createRequestDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        task = new Task();
        task.setId(10L);
        task.setName("Test Task");

        comment = new Comment();
        comment.setId(100L);
        comment.setTask(task);
        comment.setUser(user);
        comment.setText("Looks good!");
        comment.setTimestamp(LocalDateTime.of(2026, 11, 12, 12, 0));

        commentResponseDto = new CommentResponseDto();
        commentResponseDto.setId(100L);
        commentResponseDto.setTaskId(10L);
        commentResponseDto.setUserId(1L);
        commentResponseDto.setText("Looks good!");
        commentResponseDto.setTimestamp(LocalDateTime.of(2026, 11, 12, 12, 0));

        createRequestDto = new CommentCreateRequestDto();
        createRequestDto.setTaskId(10L);
        createRequestDto.setText("Looks good!");
    }

    @Test
    @DisplayName("save: successfully saves a new comment for a task")
    void save_ValidRequest_Success() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(commentMapper.toModel(createRequestDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentResponseDto);

        CommentResponseDto result = commentService.save(user, createRequestDto);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(10L, result.getTaskId());
        assertEquals(1L, result.getUserId());
        assertEquals("Looks good!", result.getText());
        assertNotNull(comment.getTimestamp());
        verify(commentRepository).save(comment);
    }

    @Test
    @DisplayName("save: throws EntityNotFoundException when task does not exist")
    void save_TaskNotFound_ThrowsEntityNotFoundException() {
        when(taskRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.save(user, createRequestDto));
    }

    @Test
    @DisplayName("findAllByTaskId: returns list of comments for existing task")
    void findAllByTaskId_ExistingTaskId_ReturnsCommentList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> commentPage = new PageImpl<>(List.of(comment));

        when(taskRepository.existsById(10L)).thenReturn(true);
        when(commentRepository.findAllByTaskId(10L, pageable)).thenReturn(commentPage);
        when(commentMapper.toDto(comment)).thenReturn(commentResponseDto);

        List<CommentResponseDto> result = commentService.findAllByTaskId(10L, pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Looks good!", result.get(0).getText());
        verify(commentRepository).findAllByTaskId(10L, pageable);
    }

    @Test
    @DisplayName("findAllByTaskId: throws EntityNotFoundException when task does not exist")
    void findAllByTaskId_NonExistingTaskId_ThrowsEntityNotFoundException() {
        Pageable pageable = PageRequest.of(0, 10);
        when(taskRepository.existsById(999L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> commentService.findAllByTaskId(999L, pageable));
    }

    @Test
    @DisplayName("deleteById: successfully deletes existing comment")
    void deleteById_ExistingId_Success() {
        when(commentRepository.existsById(100L)).thenReturn(true);

        commentService.deleteById(100L);

        verify(commentRepository).deleteById(100L);
    }

    @Test
    @DisplayName("deleteById: throws EntityNotFoundException when comment does not exist")
    void deleteById_NonExistingId_ThrowsEntityNotFoundException() {
        when(commentRepository.existsById(999L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> commentService.deleteById(999L));
    }
}

