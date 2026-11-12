package task_management_app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import task_management_app.dto.comment.CommentCreateRequestDto;
import task_management_app.dto.comment.CommentResponseDto;
import task_management_app.model.User;
import task_management_app.service.CommentService;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private ObjectMapper objectMapper;
    private User testUser;
    private CommentResponseDto testResponseDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testResponseDto = new CommentResponseDto();
        testResponseDto.setId(100L);
        testResponseDto.setTaskId(10L);
        testResponseDto.setUserId(1L);
        testResponseDto.setText("Looks good!");
        testResponseDto.setTimestamp(LocalDateTime.of(2026, 11, 12, 12, 0));

        HandlerMethodArgumentResolver authPrincipalResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
            }

            @Override
            public Object resolveArgument(
                    MethodParameter parameter,
                    ModelAndViewContainer mavContainer,
                    NativeWebRequest webRequest,
                    WebDataBinderFactory binderFactory
            ) {
                return testUser;
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                .setCustomArgumentResolvers(
                        authPrincipalResolver,
                        new PageableHandlerMethodArgumentResolver()
                )
                .build();
    }

    @Test
    @DisplayName("POST /api/comments: creates a new comment and returns 201 Created")
    void createComment_ValidRequest_ReturnsCreated() throws Exception {
        CommentCreateRequestDto requestDto = new CommentCreateRequestDto();
        requestDto.setTaskId(10L);
        requestDto.setText("Looks good!");

        when(commentService.save(any(User.class), any(CommentCreateRequestDto.class)))
                .thenReturn(testResponseDto);

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.taskId").value(10L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.text").value("Looks good!"));

        verify(commentService).save(any(User.class), any(CommentCreateRequestDto.class));
    }

    @Test
    @DisplayName("POST /api/comments: returns 400 Bad Request when text is blank")
    void createComment_BlankText_ReturnsBadRequest() throws Exception {
        CommentCreateRequestDto requestDto = new CommentCreateRequestDto();
        requestDto.setTaskId(10L);
        requestDto.setText("");

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/comments?taskId=10: returns comments for the specified taskId")
    void getCommentsByTaskId_ReturnsCommentList() throws Exception {
        when(commentService.findAllByTaskId(eq(10L), any(Pageable.class)))
                .thenReturn(List.of(testResponseDto));

        mockMvc.perform(get("/api/comments").param("taskId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100L))
                .andExpect(jsonPath("$[0].taskId").value(10L))
                .andExpect(jsonPath("$[0].text").value("Looks good!"));

        verify(commentService).findAllByTaskId(eq(10L), any(Pageable.class));
    }

    @Test
    @DisplayName("DELETE /api/comments/{id}: deletes comment and returns 204 No Content")
    void deleteComment_ReturnsNoContent() throws Exception {
        doNothing().when(commentService).deleteById(100L);

        mockMvc.perform(delete("/api/comments/100"))
                .andExpect(status().isNoContent());

        verify(commentService).deleteById(100L);
    }
}

