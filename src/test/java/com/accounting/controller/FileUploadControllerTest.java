package com.accounting.controller;

import com.accounting.exeption.StorageException;
import com.accounting.exeption.StorageFileNotFoundException;
import com.accounting.service.fileupload.FileUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileUploadController.class)
class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileUploadService fileUploadService;

    private MockMultipartFile mockFile;
    private Path testFilePath;
    private Resource testResource;

    @BeforeEach
    void setUp() throws Exception {
        mockFile = new MockMultipartFile(
            "file",
            "test-document.pdf",
            MediaType.APPLICATION_PDF_VALUE,
            "Test file content".getBytes()
        );

        // Create a temporary test file for download tests
        testFilePath = Paths.get(System.getProperty("java.io.tmpdir"), "test-download.pdf");
        Files.deleteIfExists(testFilePath);
        Files.write(testFilePath, "Test file content for download".getBytes());
        testResource = new UrlResource(testFilePath.toUri());
    }

    @Test
    void testHandleFileUpload_Success() throws Exception {
        // Arrange
        String downloadUrl = "/files/reservation-1/test-document.pdf";
        when(fileUploadService.storeFile(any(), eq(1L))).thenReturn(downloadUrl);

        // Act & Assert
        mockMvc.perform(multipart("/files")
                .file(mockFile)
                .param("reservationId", "1"))
            .andExpect(status().isOk())
            .andExpect(content().string(downloadUrl));

        verify(fileUploadService, times(1)).storeFile(any(), eq(1L));
    }

    @Test
    void testHandleFileUpload_EmptyFile_ReturnsBadRequest() throws Exception {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
            "file",
            "empty.pdf",
            MediaType.APPLICATION_PDF_VALUE,
            new byte[0]
        );

        when(fileUploadService.storeFile(any(), eq(1L)))
            .thenThrow(new StorageException("Cannot store empty file"));

        // Act & Assert
        mockMvc.perform(multipart("/files")
                .file(emptyFile)
                .param("reservationId", "1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Cannot store empty file"));

        verify(fileUploadService, times(1)).storeFile(any(), eq(1L));
    }

    @Test
    void testHandleFileUpload_InvalidFileName_ReturnsBadRequest() throws Exception {
        // Arrange
        MockMultipartFile invalidFile = new MockMultipartFile(
            "file",
            "../../../etc/passwd",
            MediaType.TEXT_PLAIN_VALUE,
            "malicious content".getBytes()
        );

        when(fileUploadService.storeFile(any(), eq(1L)))
            .thenThrow(new StorageException("Invalid file path"));

        // Act & Assert
        mockMvc.perform(multipart("/files")
                .file(invalidFile)
                .param("reservationId", "1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid file path"));

        verify(fileUploadService, times(1)).storeFile(any(), eq(1L));
    }

    @Test
    void testHandleFileUpload_StorageException_ReturnsBadRequest() throws Exception {
        // Arrange
        when(fileUploadService.storeFile(any(), eq(1L)))
            .thenThrow(new StorageException("Failed to store file"));

        // Act & Assert
        mockMvc.perform(multipart("/files")
                .file(mockFile)
                .param("reservationId", "1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Failed to store file"));

        verify(fileUploadService, times(1)).storeFile(any(), eq(1L));
    }

    @Test
    void testHandleFileUpload_UnexpectedError_ReturnsInternalServerError() throws Exception {
        // Arrange
        when(fileUploadService.storeFile(any(), eq(1L)))
            .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        mockMvc.perform(multipart("/files")
                .file(mockFile)
                .param("reservationId", "1"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("An unexpected error occurred"));

        verify(fileUploadService, times(1)).storeFile(any(), eq(1L));
    }

    @Test
    void testHandleFileUpload_MissingReservationId_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(multipart("/files")
                .file(mockFile))
            .andExpect(status().isBadRequest());

        verify(fileUploadService, never()).storeFile(any(), anyLong());
    }

    @Test
    void testDownloadFile_Success() throws Exception {
        // Arrange
        when(fileUploadService.getFilePath("reservation-1", "test-document.pdf"))
            .thenReturn(testFilePath);

        // Act & Assert
        mockMvc.perform(get("/files/reservation-1/test-document.pdf"))
            .andExpect(status().isOk())
            .andExpect(header().exists("Content-Disposition"))
            .andExpect(header().string("Content-Disposition", "attachment; filename=\"test-download.pdf\""))
            .andExpect(content().bytes("Test file content for download".getBytes()));

        verify(fileUploadService, times(1)).getFilePath("reservation-1", "test-document.pdf");
    }

    @Test
    void testDownloadFile_FileNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        Path nonExistentPath = Paths.get(System.getProperty("java.io.tmpdir"), "nonexistent.pdf");
        when(fileUploadService.getFilePath("reservation-1", "nonexistent.pdf"))
            .thenReturn(nonExistentPath);

        // Act & Assert
        mockMvc.perform(get("/files/reservation-1/nonexistent.pdf"))
            .andExpect(status().isNotFound());

        verify(fileUploadService, times(1)).getFilePath("reservation-1", "nonexistent.pdf");
    }

    @Test
    void testDownloadFile_IOError_ReturnsInternalServerError() throws Exception {
        // Arrange
        when(fileUploadService.getFilePath("reservation-1", "error.pdf"))
            .thenThrow(new IOException("Disk read error"));

        // Act & Assert
        mockMvc.perform(get("/files/reservation-1/error.pdf"))
            .andExpect(status().isInternalServerError());

        verify(fileUploadService, times(1)).getFilePath("reservation-1", "error.pdf");
    }

    @Test
    void testDownloadFile_WithDifferentContentType_Success() throws Exception {
        // Arrange
        Path imagePath = Paths.get(System.getProperty("java.io.tmpdir"), "test-image.jpg");
        Files.deleteIfExists(imagePath);
        Files.write(imagePath, "fake image content".getBytes());

        when(fileUploadService.getFilePath("reservation-1", "test-image.jpg"))
            .thenReturn(imagePath);

        // Act & Assert
        mockMvc.perform(get("/files/reservation-1/test-image.jpg"))
            .andExpect(status().isOk())
            .andExpect(header().exists("Content-Disposition"));

        verify(fileUploadService, times(1)).getFilePath("reservation-1", "test-image.jpg");

        // Cleanup
        Files.deleteIfExists(imagePath);
    }

    @Test
    void testHandleStorageFileNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(fileUploadService.storeFile(any(), eq(1L)))
            .thenThrow(new StorageFileNotFoundException("File not found"));

        // Act & Assert
        mockMvc.perform(multipart("/files")
                .file(mockFile)
                .param("reservationId", "1"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("File not found"));

        verify(fileUploadService, times(1)).storeFile(any(), eq(1L));
    }

    @Test
    void testHandleFileUpload_LargeFile_Success() throws Exception {
        // Arrange
        byte[] largeContent = new byte[1024 * 1024]; // 1MB
        MockMultipartFile largeFile = new MockMultipartFile(
            "file",
            "large-document.pdf",
            MediaType.APPLICATION_PDF_VALUE,
            largeContent
        );

        String downloadUrl = "/files/reservation-1/large-document.pdf";
        when(fileUploadService.storeFile(any(), eq(1L))).thenReturn(downloadUrl);

        // Act & Assert
        mockMvc.perform(multipart("/files")
                .file(largeFile)
                .param("reservationId", "1"))
            .andExpect(status().isOk())
            .andExpect(content().string(downloadUrl));

        verify(fileUploadService, times(1)).storeFile(any(), eq(1L));
    }

    @Test
    void testHandleFileUpload_SpecialCharactersInFileName_Success() throws Exception {
        // Arrange
        MockMultipartFile specialFile = new MockMultipartFile(
            "file",
            "test document with spaces & special chars.pdf",
            MediaType.APPLICATION_PDF_VALUE,
            "Test content".getBytes()
        );

        String downloadUrl = "/files/reservation-1/test-document-with-spaces-special-chars.pdf";
        when(fileUploadService.storeFile(any(), eq(1L))).thenReturn(downloadUrl);

        // Act & Assert
        mockMvc.perform(multipart("/files")
                .file(specialFile)
                .param("reservationId", "1"))
            .andExpect(status().isOk())
            .andExpect(content().string(downloadUrl));

        verify(fileUploadService, times(1)).storeFile(any(), eq(1L));
    }
}
