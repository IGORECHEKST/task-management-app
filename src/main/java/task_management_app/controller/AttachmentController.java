package task_management_app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import task_management_app.dto.attachment.AttachmentResponseDto;
import task_management_app.model.Attachment;
import task_management_app.service.AttachmentService;

@Tag(name = "Attachment Management", description = "Endpoints for managing task attachments via Dropbox")
@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Upload an attachment", description = "Upload a file to Dropbox and link it to a task")
    public AttachmentResponseDto uploadAttachment(
            @RequestParam Long taskId,
            @RequestPart("file") MultipartFile file
    ) {
        return attachmentService.upload(taskId, file);
    }

    @GetMapping
    @Operation(summary = "Retrieve attachments for a task", description = "Retrieve attachment metadata by taskId")
    public List<AttachmentResponseDto> getAttachmentsByTaskId(@RequestParam Long taskId) {
        return attachmentService.findAllByTaskId(taskId);
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download an attachment", description = "Download the actual file from Dropbox by ID")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long id) {
        Attachment attachment = attachmentService.findEntityById(id);
        byte[] content = attachmentService.download(id);
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getFilename() + "\""
                )
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an attachment", description = "Delete an attachment from Dropbox and the database")
    public void deleteAttachment(@PathVariable Long id) {
        attachmentService.deleteById(id);
    }
}

