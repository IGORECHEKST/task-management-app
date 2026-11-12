package task_management_app.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import task_management_app.dto.comment.CommentCreateRequestDto;
import task_management_app.dto.comment.CommentResponseDto;
import task_management_app.model.User;

public interface CommentService {
    CommentResponseDto save(User user, CommentCreateRequestDto requestDto);

    List<CommentResponseDto> findAllByTaskId(Long taskId, Pageable pageable);

    void deleteById(Long id);
}

