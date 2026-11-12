package task_management_app.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
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
import org.springframework.mock.web.MockMultipartFile;
import task_management_app.dto.attachment.AttachmentResponseDto;
import task_management_app.exception.EntityNotFoundException;
import task_management_app.mapper.AttachmentMapper;
import task_management_app.model.Attachment;
import task_management_app.model.Task;
import task_management_app.repository.AttachmentRepository;
import task_management_app.repository.TaskRepository;
import task_management_app.service.impl.AttachmentServiceImpl;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private DropboxService dropboxService;

    @Mock
    private AttachmentMapper attachmentMapper;

    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    private Task task;
    private Attachment attachment;
    private AttachmentResponseDto responseDto;
    private MockMultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(10L);
        task.setName("Test Task");

        attachment = new Attachment();
        attachment.setId(100L);
        attachment.setTask(task);
        attachment.setDropboxFileId("id:dropbox123");
        attachment.setFilename("report.pdf");
        attachment.setUploadDate(LocalDateTime.of(2026, 11, 12, 15, 0));

        responseDto = new AttachmentResponseDto();
        responseDto.setId(100L);
        responseDto.setTaskId(10L);
        responseDto.setDropboxFileId("id:dropbox123");
        responseDto.setFilename("report.pdf");
        responseDto.setUploadDate(LocalDateTime.of(2026, 11, 12, 15, 0));

        multipartFile = new MockMultipartFile(
                "file",
                "report.pdf",
                "application/pdf",
                "PDF content".getBytes(StandardCharsets.UTF_8)
        );
    }

    @Test
    @DisplayName("upload: uploads file to Dropbox and saves attachment metadata")
    void upload_ValidTaskAndFile_Success() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(dropboxService.uploadFile(multipartFile)).thenReturn("id:dropbox123");
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(attachment);
        when(attachmentMapper.toDto(attachment)).thenReturn(responseDto);

        AttachmentResponseDto result = attachmentService.upload(10L, multipartFile);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(10L, result.getTaskId());
        assertEquals("id:dropbox123", result.getDropboxFileId());
        assertEquals("report.pdf", result.getFilename());
        verify(dropboxService).uploadFile(multipartFile);
        verify(attachmentRepository).save(any(Attachment.class));
    }

    @Test
    @DisplayName("upload: throws EntityNotFoundException when task does not exist")
    void upload_TaskNotFound_ThrowsEntityNotFoundException() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> attachmentService.upload(999L, multipartFile));
    }

    @Test
    @DisplayName("findAllByTaskId: returns attachments for existing task")
    void findAllByTaskId_ExistingTask_ReturnsAttachmentList() {
        when(taskRepository.existsById(10L)).thenReturn(true);
        when(attachmentRepository.findAllByTaskId(10L)).thenReturn(List.of(attachment));
        when(attachmentMapper.toDto(attachment)).thenReturn(responseDto);

        List<AttachmentResponseDto> result = attachmentService.findAllByTaskId(10L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("report.pdf", result.get(0).getFilename());
        verify(attachmentRepository).findAllByTaskId(10L);
    }

    @Test
    @DisplayName("findAllByTaskId: throws EntityNotFoundException when task does not exist")
    void findAllByTaskId_NonExistingTask_ThrowsEntityNotFoundException() {
        when(taskRepository.existsById(999L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> attachmentService.findAllByTaskId(999L));
    }

    @Test
    @DisplayName("download: downloads file bytes from Dropbox for existing attachment")
    void download_ExistingAttachment_ReturnsBytes() {
        byte[] expectedBytes = "PDF content".getBytes(StandardCharsets.UTF_8);
        when(attachmentRepository.findById(100L)).thenReturn(Optional.of(attachment));
        when(dropboxService.downloadFile("id:dropbox123")).thenReturn(expectedBytes);

        byte[] result = attachmentService.download(100L);

        assertArrayEquals(expectedBytes, result);
        verify(dropboxService).downloadFile("id:dropbox123");
    }

    @Test
    @DisplayName("deleteById: deletes file from Dropbox and removes attachment from DB")
    void deleteById_ExistingAttachment_Success() {
        when(attachmentRepository.findById(100L)).thenReturn(Optional.of(attachment));

        attachmentService.deleteById(100L);

        verify(dropboxService).deleteFile("id:dropbox123");
        verify(attachmentRepository).delete(attachment);
    }

    @Test
    @DisplayName("deleteById: throws EntityNotFoundException when attachment does not exist")
    void deleteById_NonExistingAttachment_ThrowsEntityNotFoundException() {
        when(attachmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> attachmentService.deleteById(999L));
    }
}

