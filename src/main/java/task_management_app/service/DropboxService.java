package task_management_app.service;

import org.springframework.web.multipart.MultipartFile;

public interface DropboxService {
    String uploadFile(MultipartFile file);

    byte[] downloadFile(String dropboxFileId);

    void deleteFile(String dropboxFileId);
}

