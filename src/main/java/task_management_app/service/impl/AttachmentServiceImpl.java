package task_management_app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import task_management_app.dto.attachment.AttachmentResponseDto;
import task_management_app.exception.EntityNotFoundException;
import task_management_app.mapper.AttachmentMapper;
import task_management_app.model.Attachment;
import task_management_app.model.Task;
import task_management_app.repository.AttachmentRepository;
import task_management_app.repository.TaskRepository;
import task_management_app.service.AttachmentService;
import task_management_app.service.DropboxService;

@Service
@RequiredArgsConstructor
@Transactional
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final DropboxService dropboxService;
    private final AttachmentMapper attachmentMapper;

    @Override
    public AttachmentResponseDto upload(Long taskId, MultipartFile file) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find task by id: " + taskId));

        String dropboxFileId = dropboxService.uploadFile(file);

        Attachment attachment = new Attachment();
        attachment.setTask(task);
        attachment.setDropboxFileId(dropboxFileId);
        attachment.setFilename(file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
        attachment.setUploadDate(LocalDateTime.now());

        Attachment savedAttachment = attachmentRepository.save(attachment);
        return attachmentMapper.toDto(savedAttachment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentResponseDto> findAllByTaskId(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new EntityNotFoundException("Can't find task by id: " + taskId);
        }
        return attachmentRepository.findAllByTaskId(taskId).stream()
                .map(attachmentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Attachment findEntityById(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find attachment by id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] download(Long id) {
        Attachment attachment = findEntityById(id);
        return dropboxService.downloadFile(attachment.getDropboxFileId());
    }

    @Override
    public void deleteById(Long id) {
        Attachment attachment = findEntityById(id);
        dropboxService.deleteFile(attachment.getDropboxFileId());
        attachmentRepository.delete(attachment);
    }
}

