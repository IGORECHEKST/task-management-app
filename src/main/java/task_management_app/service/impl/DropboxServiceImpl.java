package task_management_app.service.impl;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import task_management_app.exception.FileStorageException;
import task_management_app.service.DropboxService;

@Service
@RequiredArgsConstructor
public class DropboxServiceImpl implements DropboxService {

    private final DbxClientV2 dropboxClient;

    @Override
    public String uploadFile(MultipartFile file) {
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
        String dropboxPath = "/" + UUID.randomUUID() + "_" + filename;
        try (InputStream inputStream = file.getInputStream()) {
            FileMetadata metadata = dropboxClient.files()
                    .uploadBuilder(dropboxPath)
                    .uploadAndFinish(inputStream);
            return metadata.getId();
        } catch (DbxException | IOException e) {
            throw new FileStorageException("Can't upload file to Dropbox: " + filename, e);
        }
    }

    @Override
    public byte[] downloadFile(String dropboxFileId) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            dropboxClient.files()
                    .download(dropboxFileId)
                    .download(outputStream);
            return outputStream.toByteArray();
        } catch (DbxException | IOException e) {
            throw new FileStorageException("Can't download file from Dropbox: " + dropboxFileId, e);
        }
    }

    @Override
    public void deleteFile(String dropboxFileId) {
        try {
            dropboxClient.files().deleteV2(dropboxFileId);
        } catch (DbxException e) {
            throw new FileStorageException("Can't delete file from Dropbox: " + dropboxFileId, e);
        }
    }
}

