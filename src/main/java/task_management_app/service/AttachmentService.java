package task_management_app.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import task_management_app.dto.attachment.AttachmentResponseDto;
import task_management_app.model.Attachment;

public interface AttachmentService {
    AttachmentResponseDto upload(Long taskId, MultipartFile file);

    List<AttachmentResponseDto> findAllByTaskId(Long taskId);

    Attachment findEntityById(Long id);

    byte[] download(Long id);

    void deleteById(Long id);
}

