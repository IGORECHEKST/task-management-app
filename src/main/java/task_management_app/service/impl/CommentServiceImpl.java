package task_management_app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task_management_app.dto.comment.CommentCreateRequestDto;
import task_management_app.dto.comment.CommentResponseDto;
import task_management_app.exception.EntityNotFoundException;
import task_management_app.mapper.CommentMapper;
import task_management_app.model.Comment;
import task_management_app.model.Task;
import task_management_app.model.User;
import task_management_app.repository.CommentRepository;
import task_management_app.repository.TaskRepository;
import task_management_app.service.CommentService;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentResponseDto save(User user, CommentCreateRequestDto requestDto) {
        Task task = taskRepository.findById(requestDto.getTaskId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find task by id: " + requestDto.getTaskId()));

        Comment comment = commentMapper.toModel(requestDto);
        comment.setTask(task);
        comment.setUser(user);
        comment.setTimestamp(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> findAllByTaskId(Long taskId, Pageable pageable) {
        if (!taskRepository.existsById(taskId)) {
            throw new EntityNotFoundException("Can't find task by id: " + taskId);
        }
        return commentRepository.findAllByTaskId(taskId, pageable).stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new EntityNotFoundException("Can't find comment by id: " + id);
        }
        commentRepository.deleteById(id);
    }
}

