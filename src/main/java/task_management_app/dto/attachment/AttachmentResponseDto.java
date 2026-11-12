package task_management_app.dto.attachment;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AttachmentResponseDto {
    private Long id;
    private Long taskId;
    private String dropboxFileId;
    private String filename;
    private LocalDateTime uploadDate;
}

