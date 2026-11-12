package task_management_app.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DbxUserFilesRequests;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadBuilder;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import task_management_app.exception.FileStorageException;
import task_management_app.service.impl.DropboxServiceImpl;

@ExtendWith(MockitoExtension.class)
class DropboxServiceTest {

    @Mock
    private DbxClientV2 dropboxClient;

    @Mock
    private DbxUserFilesRequests filesRequests;

    @InjectMocks
    private DropboxServiceImpl dropboxService;

    private MockMultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        multipartFile = new MockMultipartFile(
                "file",
                "doc.txt",
                "text/plain",
                "Hello Dropbox".getBytes(StandardCharsets.UTF_8)
        );
    }

    @Test
    @DisplayName("uploadFile: uploads stream to Dropbox and returns file ID")
    void uploadFile_ValidFile_ReturnsFileId() throws Exception {
        UploadBuilder uploadBuilder = mock(UploadBuilder.class);
        FileMetadata metadata = mock(FileMetadata.class);

        when(dropboxClient.files()).thenReturn(filesRequests);
        when(filesRequests.uploadBuilder(anyString())).thenReturn(uploadBuilder);
        when(uploadBuilder.uploadAndFinish(any(InputStream.class))).thenReturn(metadata);
        when(metadata.getId()).thenReturn("id:dropbox-file-1");

        String fileId = dropboxService.uploadFile(multipartFile);

        assertEquals("id:dropbox-file-1", fileId);
    }

    @Test
    @DisplayName("downloadFile: downloads bytes from Dropbox")
    void downloadFile_ValidId_ReturnsBytes() throws Exception {
        @SuppressWarnings("unchecked")
        DbxDownloader<FileMetadata> downloader = mock(DbxDownloader.class);
        byte[] expected = "Hello Dropbox".getBytes(StandardCharsets.UTF_8);

        when(dropboxClient.files()).thenReturn(filesRequests);
        when(filesRequests.download("id:dropbox-file-1")).thenReturn(downloader);
        doAnswer(invocation -> {
            OutputStream out = invocation.getArgument(0);
            out.write(expected);
            return null;
        }).when(downloader).download(any(OutputStream.class));

        byte[] result = dropboxService.downloadFile("id:dropbox-file-1");

        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("deleteFile: deletes file in Dropbox")
    void deleteFile_ValidId_Success() throws Exception {
        when(dropboxClient.files()).thenReturn(filesRequests);

        dropboxService.deleteFile("id:dropbox-file-1");

        verify(filesRequests).deleteV2("id:dropbox-file-1");
    }

    @Test
    @DisplayName("deleteFile: wraps DbxException into FileStorageException")
    void deleteFile_DbxException_ThrowsFileStorageException() throws Exception {
        when(dropboxClient.files()).thenReturn(filesRequests);
        when(filesRequests.deleteV2("id:bad")).thenThrow(new DbxException("Dropbox error"));

        assertThrows(FileStorageException.class, () -> dropboxService.deleteFile("id:bad"));
    }
}

