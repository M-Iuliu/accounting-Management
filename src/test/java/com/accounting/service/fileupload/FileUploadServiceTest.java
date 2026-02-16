package com.accounting.service.fileupload;

import com.accounting.exeption.StorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FileUploadServiceTest {

    @TempDir
    Path tempDir;

    private FileUploadServiceImpl fileUploadService;

    @BeforeEach
    void setUp() {
        // Initialize FileUploadService with temp directory
        fileUploadService = new FileUploadServiceImpl(tempDir.toString());
    }

    @Test
    void testStoreFile_Success() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test-document.pdf",
            "application/pdf",
            "Test file content".getBytes()
        );
        Long reservationId = 1L;

        // Act
        String downloadUrl = fileUploadService.storeFile(file, reservationId);

        // Assert
        assertNotNull(downloadUrl);
        assertTrue(downloadUrl.contains("reservation-1"));
        assertTrue(downloadUrl.contains("test-document.pdf"));

        // Verify file was actually created
        Path reservationDir = tempDir.resolve("reservation-1");
        assertTrue(Files.exists(reservationDir));
        Path savedFile = reservationDir.resolve("test-document.pdf");
        assertTrue(Files.exists(savedFile));
        assertEquals("Test file content", Files.readString(savedFile));
    }

    @Test
    void testStoreFile_EmptyFile_ThrowsException() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
            "file",
            "empty.txt",
            "text/plain",
            new byte[0]
        );
        Long reservationId = 1L;

        // Act & Assert
        StorageException exception = assertThrows(
            StorageException.class,
            () -> fileUploadService.storeFile(emptyFile, reservationId)
        );
        assertEquals("Failed to store empty file.", exception.getMessage());
    }

    @Test
    void testStoreFile_DirectoryTraversalPrevention_ThrowsException() {
        // Arrange
        MockMultipartFile maliciousFile = new MockMultipartFile(
            "file",
            "../../../etc/passwd",
            "text/plain",
            "Malicious content".getBytes()
        );
        Long reservationId = 1L;

        // Act & Assert
        StorageException exception = assertThrows(
            StorageException.class,
            () -> fileUploadService.storeFile(maliciousFile, reservationId)
        );
        assertEquals("Cannot store file outside current directory.", exception.getMessage());
    }

    @Test
    void testStoreFile_ReplaceExisting_Success() throws IOException {
        // Arrange
        MockMultipartFile file1 = new MockMultipartFile(
            "file",
            "document.txt",
            "text/plain",
            "Original content".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
            "file",
            "document.txt",
            "text/plain",
            "Updated content".getBytes()
        );
        Long reservationId = 1L;

        // Act
        fileUploadService.storeFile(file1, reservationId);
        String downloadUrl = fileUploadService.storeFile(file2, reservationId);

        // Assert
        assertNotNull(downloadUrl);
        Path savedFile = tempDir.resolve("reservation-1").resolve("document.txt");
        assertEquals("Updated content", Files.readString(savedFile));
    }

    @Test
    void testGetFilePath_Success() {
        // Arrange
        String folder = "reservation-1";
        String filename = "test.pdf";

        // Act
        Path result = fileUploadService.getFilePath(folder, filename);

        // Assert
        assertNotNull(result);
        assertTrue(result.toString().contains("reservation-1"));
        assertTrue(result.toString().contains("test.pdf"));
    }

    @Test
    void testGetAllDownloadUrlsForReservation_WithFiles_Success() throws IOException {
        // Arrange
        Long reservationId = 1L;
        MockMultipartFile file1 = new MockMultipartFile(
            "file",
            "document1.pdf",
            "application/pdf",
            "Content 1".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
            "file",
            "document2.pdf",
            "application/pdf",
            "Content 2".getBytes()
        );

        fileUploadService.storeFile(file1, reservationId);
        fileUploadService.storeFile(file2, reservationId);

        // Act
        List<String> urls = fileUploadService.getAllDownloadUrlsForReservation(reservationId);

        // Assert
        assertNotNull(urls);
        assertEquals(2, urls.size());
        assertTrue(urls.stream().anyMatch(url -> url.contains("document1.pdf")));
        assertTrue(urls.stream().anyMatch(url -> url.contains("document2.pdf")));
    }

    @Test
    void testGetAllDownloadUrlsForReservation_NoFiles_ReturnsEmpty() {
        // Arrange
        Long reservationId = 999L;

        // Act
        List<String> urls = fileUploadService.getAllDownloadUrlsForReservation(reservationId);

        // Assert
        assertNotNull(urls);
        assertTrue(urls.isEmpty());
    }

    @Test
    void testDeleteAll_Success() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "Content".getBytes()
        );
        fileUploadService.storeFile(file, 1L);

        // Verify file exists
        assertTrue(Files.exists(tempDir.resolve("reservation-1")));

        // Act
        fileUploadService.deleteAll();

        // Assert
        assertFalse(Files.exists(tempDir));
    }

    @Test
    void testLoadAsResource_NotImplemented_ReturnsNull() {
        // Act
        var result = fileUploadService.loadAsResource("test.txt");

        // Assert
        assertNull(result);
    }

    @Test
    void testLoad_NotImplemented_ReturnsNull() {
        // Act
        Path result = fileUploadService.load("test.txt");

        // Assert
        assertNull(result);
    }

    @Test
    void testLoadAll_NotImplemented_ReturnsEmptyStream() {
        // Act
        var result = fileUploadService.loadAll();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.count());
    }

    @Test
    void testConstructor_EmptyDirectory_ThrowsException() {
        // Act & Assert
        StorageException exception = assertThrows(
            StorageException.class,
            () -> new FileUploadServiceImpl("   ")
        );
        assertEquals("File upload location can not be Empty.", exception.getMessage());
    }
}
