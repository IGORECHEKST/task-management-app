package task_management_app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import task_management_app.dto.attachment.AttachmentResponseDto;
import task_management_app.model.Attachment;
import task_management_app.service.AttachmentService;

@ExtendWith(MockitoExtension.class)
class AttachmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AttachmentService attachmentService;

    @InjectMocks
    private AttachmentController attachmentController;

    private AttachmentResponseDto testResponseDto;
    private Attachment testAttachment;

    @BeforeEach
    void setUp() {
        testAttachment = new Attachment();
        testAttachment.setId(100L);
        testAttachment.setDropboxFileId("id:dropbox123");
        testAttachment.setFilename("report.pdf");
        testAttachment.setUploadDate(LocalDateTime.of(2026, 11, 12, 15, 0));

        testResponseDto = new AttachmentResponseDto();
        testResponseDto.setId(100L);
        testResponseDto.setTaskId(10L);
        testResponseDto.setDropboxFileId("id:dropbox123");
        testResponseDto.setFilename("report.pdf");
        testResponseDto.setUploadDate(LocalDateTime.of(2026, 11, 12, 15, 0));

        mockMvc = MockMvcBuilders.standaloneSetup(attachmentController).build();
    }

    @Test
    @DisplayName("POST /api/attachments: uploads attachment and returns 201 Created")
    void uploadAttachment_ValidRequest_ReturnsCreated() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "report.pdf",
                "application/pdf",
                "PDF content".getBytes(StandardCharsets.UTF_8)
        );

        when(attachmentService.upload(eq(10L), any(MultipartFile.class)))
                .thenReturn(testResponseDto);

        mockMvc.perform(multipart("/api/attachments")
                        .file(file)
                        .param("taskId", "10"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.taskId").value(10L))
                .andExpect(jsonPath("$.dropboxFileId").value("id:dropbox123"))
                .andExpect(jsonPath("$.filename").value("report.pdf"));

        verify(attachmentService).upload(eq(10L), any(MultipartFile.class));
    }

    @Test
    @DisplayName("GET /api/attachments?taskId=10: returns attachments for the task")
    void getAttachmentsByTaskId_ReturnsAttachmentList() throws Exception {
        when(attachmentService.findAllByTaskId(10L)).thenReturn(List.of(testResponseDto));

        mockMvc.perform(get("/api/attachments").param("taskId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100L))
                .andExpect(jsonPath("$[0].filename").value("report.pdf"));

        verify(attachmentService).findAllByTaskId(10L);
    }

    @Test
    @DisplayName("GET /api/attachments/{id}/download: downloads file from Dropbox")
    void downloadAttachment_ReturnsFileBytes() throws Exception {
        byte[] content = "PDF content".getBytes(StandardCharsets.UTF_8);
        when(attachmentService.findEntityById(100L)).thenReturn(testAttachment);
        when(attachmentService.download(100L)).thenReturn(content);

        mockMvc.perform(get("/api/attachments/100/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"report.pdf\""
                ))
                .andExpect(content().bytes(content));

        verify(attachmentService).findEntityById(100L);
        verify(attachmentService).download(100L);
    }

    @Test
    @DisplayName("DELETE /api/attachments/{id}: deletes attachment and returns 204 No Content")
    void deleteAttachment_ReturnsNoContent() throws Exception {
        doNothing().when(attachmentService).deleteById(100L);

        mockMvc.perform(delete("/api/attachments/100"))
                .andExpect(status().isNoContent());

        verify(attachmentService).deleteById(100L);
    }
}

