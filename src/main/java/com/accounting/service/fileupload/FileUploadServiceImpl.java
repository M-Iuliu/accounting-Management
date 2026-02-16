package com.accounting.service.fileupload;

import com.accounting.exeption.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.accounting.constants.Constants.RESERVATION_PREFIX;

/**
 * Service implementation for file upload and management operations.
 * Handles storing, retrieving, and managing files on the file system.
 */
@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    private final Path rootLocation;

    @Autowired
    public FileUploadServiceImpl(@Value("${file.upload-dir:uploads}") String baseDir) {
        log.info("Initializing FileUploadService with upload directory: {}", baseDir);

        if (baseDir.trim().isEmpty()) {
            log.error("File upload location cannot be empty");
            throw new StorageException("File upload location can not be Empty.");
        }

        this.rootLocation = Paths.get(baseDir).toAbsolutePath().normalize();
        log.debug("Root location resolved to: {}", this.rootLocation);

        try {
            Files.createDirectories(rootLocation);
            log.info("Successfully initialized storage directory: {}", rootLocation);
        } catch (IOException e) {
            log.error("Could not initialize storage directory: {}", rootLocation, e);
            throw new StorageException("Could not initialize storage", e);
        }
    }

    /**
     * Stores an uploaded file for a specific reservation
     * @param file the multipart file to store
     * @param reservationId the reservation ID this file belongs to
     * @return the download URL for the stored file
     * @throws StorageException if storage fails
     */
    @Override
    public String storeFile(MultipartFile file, Long reservationId) {
        log.info("Storing file for reservation ID: {}, filename: {}",
            reservationId, file.getOriginalFilename());

        try {
            if (file.isEmpty()) {
                log.warn("Attempted to store empty file for reservation {}", reservationId);
                throw new StorageException("Failed to store empty file.");
            }

            String folderName = RESERVATION_PREFIX + reservationId;
            Path reservationDir = rootLocation.resolve(folderName);
            Files.createDirectories(reservationDir);
            log.debug("Created/verified reservation directory: {}", reservationDir);

            String originalFilename = Path.of(Objects.requireNonNull(file.getOriginalFilename()))
                    .getFileName().toString();

            Path targetLocation = reservationDir.resolve(originalFilename).normalize().toAbsolutePath();

            // Security check: prevent directory traversal attacks
            if (!targetLocation.startsWith(this.rootLocation.toAbsolutePath())) {
                log.error("Attempted to store file outside allowed directory: {}", targetLocation);
                throw new StorageException("Cannot store file outside current directory.");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
                log.info("Successfully stored file: {} for reservation {}", originalFilename, reservationId);
            }

            // Build download URL
            String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/files/download/")
                    .path(folderName + "/")
                    .path(originalFilename)
                    .toUriString();

            log.debug("Generated download URL: {}", downloadUrl);
            return downloadUrl;

        } catch (IOException e) {
            log.error("Failed to store file for reservation {}: {}", reservationId, e.getMessage(), e);
            throw new StorageException("Failed to store file.", e);
        }
    }

    /**
     * Gets the file path for a specific folder and filename
     * @param folder the folder name
     * @param filename the filename
     * @return the resolved path
     */
    @Override
    public Path getFilePath(String folder, String filename) {
        log.debug("Getting file path for folder: {}, filename: {}", folder, filename);
        Path filePath = rootLocation.resolve(folder).resolve(filename).normalize();
        log.debug("Resolved file path: {}", filePath);
        return filePath;
    }

    /**
     * Gets all download URLs for files belonging to a reservation
     * @param reservationId the reservation ID
     * @return list of download URLs
     */
    @Override
    public List<String> getAllDownloadUrlsForReservation(Long reservationId) {
        log.info("Getting all download URLs for reservation ID: {}", reservationId);

        String folderName = RESERVATION_PREFIX + reservationId;
        Path reservationDir = rootLocation.resolve(folderName);

        if (!Files.exists(reservationDir) || !Files.isDirectory(reservationDir)) {
            log.debug("No files found for reservation {} - directory does not exist", reservationId);
            return Collections.emptyList();
        }

        try (Stream<Path> files = Files.list(reservationDir)) {
            List<String> urls = files
                    .filter(Files::isRegularFile)
                    .map(path -> ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/files/download/")
                            .path(folderName + "/")
                            .path(path.getFileName().toString())
                            .toUriString())
                    .collect(Collectors.toList());

            log.info("Found {} files for reservation {}", urls.size(), reservationId);
            return urls;
        } catch (IOException e) {
            log.error("Could not list files for reservation {}: {}", reservationId, e.getMessage(), e);
            throw new RuntimeException("Could not list files for reservation " + reservationId, e);
        }
    }

    /**
     * Loads all files (not implemented)
     * @return empty stream
     */
    @Override
    public Stream<Path> loadAll() {
        log.debug("loadAll() called - not implemented, returning empty stream");
        return Stream.empty();
    }

    /**
     * Loads a specific file by name (not implemented)
     * @param filename the filename
     * @return null
     */
    @Override
    public Path load(String filename) {
        log.debug("load() called for filename: {} - not implemented", filename);
        return null;
    }

    /**
     * Loads a file as a resource (not implemented)
     * @param filename the filename
     * @return null
     */
    @Override
    public Resource loadAsResource(String filename) {
        log.debug("loadAsResource() called for filename: {} - not implemented", filename);
        return null;
    }

    /**
     * Deletes a specific file (not implemented)
     * @param filename the filename to delete
     */
    @Override
    public void delete(String filename) {
        log.debug("delete() called for filename: {} - not implemented", filename);
    }

    /**
     * Deletes all files in the storage directory
     */
    @Override
    public void deleteAll() {
        log.warn("Deleting all files in storage directory: {}", rootLocation);
        boolean deleted = FileSystemUtils.deleteRecursively(rootLocation.toFile());
        if (deleted) {
            log.info("Successfully deleted all files in storage directory");
        } else {
            log.warn("Failed to delete all files in storage directory");
        }
    }
}
