package task_management_app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import task_management_app.dto.label.LabelCreateRequestDto;
import task_management_app.dto.label.LabelResponseDto;
import task_management_app.dto.label.LabelUpdateRequestDto;
import task_management_app.service.LabelService;

@ExtendWith(MockitoExtension.class)
class LabelControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LabelService labelService;

    @InjectMocks
    private LabelController labelController;

    private ObjectMapper objectMapper;
    private LabelResponseDto testResponseDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        testResponseDto = new LabelResponseDto();
        testResponseDto.setId(1L);
        testResponseDto.setName("Bug");
        testResponseDto.setColor("#FF0000");

        mockMvc = MockMvcBuilders.standaloneSetup(labelController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("POST /api/labels: creates a new label and returns 201 Created")
    void createLabel_ValidRequest_ReturnsCreated() throws Exception {
        LabelCreateRequestDto requestDto = new LabelCreateRequestDto();
        requestDto.setName("Bug");
        requestDto.setColor("#FF0000");

        when(labelService.save(any(LabelCreateRequestDto.class))).thenReturn(testResponseDto);

        mockMvc.perform(post("/api/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Bug"))
                .andExpect(jsonPath("$.color").value("#FF0000"));

        verify(labelService).save(any(LabelCreateRequestDto.class));
    }

    @Test
    @DisplayName("POST /api/labels: returns 400 Bad Request when name is blank")
    void createLabel_BlankName_ReturnsBadRequest() throws Exception {
        LabelCreateRequestDto requestDto = new LabelCreateRequestDto();
        requestDto.setName("");
        requestDto.setColor("#FF0000");

        mockMvc.perform(post("/api/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/labels: returns list of labels")
    void getAllLabels_ReturnsLabelList() throws Exception {
        when(labelService.findAll(any(Pageable.class))).thenReturn(List.of(testResponseDto));

        mockMvc.perform(get("/api/labels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Bug"))
                .andExpect(jsonPath("$[0].color").value("#FF0000"));

        verify(labelService).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/labels/{id}: returns label details by ID")
    void getLabelById_ReturnsLabel() throws Exception {
        when(labelService.findById(1L)).thenReturn(testResponseDto);

        mockMvc.perform(get("/api/labels/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Bug"))
                .andExpect(jsonPath("$.color").value("#FF0000"));

        verify(labelService).findById(1L);
    }

    @Test
    @DisplayName("PUT /api/labels/{id}: updates label and returns 200 OK")
    void updateLabel_ValidRequest_ReturnsUpdatedLabel() throws Exception {
        LabelUpdateRequestDto requestDto = new LabelUpdateRequestDto();
        requestDto.setName("Critical Bug");
        requestDto.setColor("#8B0000");

        LabelResponseDto updatedDto = new LabelResponseDto();
        updatedDto.setId(1L);
        updatedDto.setName("Critical Bug");
        updatedDto.setColor("#8B0000");

        when(labelService.update(eq(1L), any(LabelUpdateRequestDto.class))).thenReturn(updatedDto);

        mockMvc.perform(put("/api/labels/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Critical Bug"))
                .andExpect(jsonPath("$.color").value("#8B0000"));

        verify(labelService).update(eq(1L), any(LabelUpdateRequestDto.class));
    }

    @Test
    @DisplayName("DELETE /api/labels/{id}: deletes label and returns 204 No Content")
    void deleteLabel_ReturnsNoContent() throws Exception {
        doNothing().when(labelService).deleteById(1L);

        mockMvc.perform(delete("/api/labels/1"))
                .andExpect(status().isNoContent());

        verify(labelService).deleteById(1L);
    }
}

