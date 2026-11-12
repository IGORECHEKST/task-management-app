package task_management_app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import task_management_app.dto.comment.CommentCreateRequestDto;
import task_management_app.dto.comment.CommentResponseDto;
import task_management_app.model.User;
import task_management_app.service.CommentService;

@Tag(name = "Comment Management", description = "Endpoints for managing task comments")
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a comment to a task", description = "Create a new comment for a specific task")
    public CommentResponseDto createComment(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CommentCreateRequestDto requestDto
    ) {
        return commentService.save(user, requestDto);
    }

    @GetMapping
    @Operation(summary = "Retrieve comments for a task", description = "Retrieve paginated comments by taskId")
    public List<CommentResponseDto> getCommentsByTaskId(
            @RequestParam Long taskId,
            Pageable pageable
    ) {
        return commentService.findAllByTaskId(taskId, pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a comment", description = "Delete a comment by its ID")
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteById(id);
    }
}

