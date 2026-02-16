package com.accounting.controller;

import com.accounting.exeption.ErrorResponse;
import com.accounting.exeption.StorageException;
import com.accounting.exeption.StorageFileNotFoundException;
import com.accounting.service.fileupload.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/**
 * REST controller for file upload and download operations.
 * Handles multipart file uploads and file retrieval for reservations.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/reservation/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * Downloads a file from a specific folder
     * @param folder the folder name
     * @param filename the filename to download
     * @return the file as a resource with appropriate content type
     */
    @GetMapping("/{folder}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable String folder,
                                                 @PathVariable String filename) {
        log.info("Download request - folder: {}, filename: {}", folder, filename);

        try {
            Path filePath = fileUploadService.getFilePath(folder, filename);
            if (!Files.exists(filePath)) {
                log.warn("File not found: {}", filePath);
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(filePath.toUri());
            String contentType = Files.probeContentType(filePath);
            contentType = (contentType != null) ? contentType : "application/octet-stream";

            log.info("Successfully serving file: {} with content type: {}", filename, contentType);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (IOException e) {
            log.error("Error downloading file {}/{}: {}", folder, filename, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Handles file upload for a specific reservation
     * @param file the multipart file to upload
     * @param reservationId the reservation ID this file belongs to
     * @return the download URL for the uploaded file
     */
    @PostMapping("/{reservationId}")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file,
                                              @PathVariable("reservationId") Long reservationId) {
        log.info("File upload request - reservation ID: {}, filename: {}, size: {} bytes",
            reservationId, file.getOriginalFilename(), file.getSize());

        try {
            String downloadUrl = fileUploadService.storeFile(file, reservationId);
            log.info("File uploaded successfully, download URL: {}", downloadUrl);
            return ResponseEntity.ok(downloadUrl);
        } catch (StorageException e) {
            log.error("Storage error while uploading file: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            log.error("Unexpected error while uploading file: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Exception handler for file not found errors
     */
    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        log.warn("Storage file not found: {}", exc.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(exc.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    /**
     * Exception handler for validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation error: {}", errorMessage);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(errorMessage, HttpStatus.BAD_REQUEST.value()));
    }
}
