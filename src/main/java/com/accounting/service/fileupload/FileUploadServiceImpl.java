package com.accounting.service.fileupload;

import com.accounting.exeption.StorageException;
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

@Service
public class FileUploadServiceImpl implements FileUploadService {


    private final Path rootLocation;

    @Autowired
    public FileUploadServiceImpl(@Value("${file.upload-dir:uploads}") String baseDir) {
        if (baseDir.trim().isEmpty()) {
            throw new StorageException("File upload location can not be Empty.");
        }

        this.rootLocation = Paths.get(baseDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file, Long reservationId) {

        try {

            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }

            String folderName = RESERVATION_PREFIX + reservationId;
            Path reservationDir = rootLocation.resolve(folderName);
            Files.createDirectories(reservationDir);

            String originalFilename = Path.of(Objects.requireNonNull(file.getOriginalFilename()))
                    .getFileName().toString();

            Path targetLocation = reservationDir.resolve(originalFilename).normalize().toAbsolutePath();

            if (!targetLocation.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new StorageException("Cannot store file outside current directory.");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            // Build download URL
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/files/download/")
                    .path(folderName + "/")
                    .path(originalFilename)
                    .toUriString();

        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    public Path getFilePath(String folder, String filename) {
        return rootLocation.resolve(folder).resolve(filename).normalize();
    }

    @Override
    public List<String> getAllDownloadUrlsForReservation(Long reservationId) {
        String folderName = RESERVATION_PREFIX + reservationId;
        Path reservationDir = rootLocation.resolve(folderName);

        if (!Files.exists(reservationDir) || !Files.isDirectory(reservationDir)) {
            return Collections.emptyList(); // or throw exception if preferred
        }

        try (Stream<Path> files = Files.list(reservationDir)) {
            return files
                    .filter(Files::isRegularFile)
                    .map(path -> ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/files/download/")
                            .path(folderName + "/")
                            .path(path.getFileName().toString())
                            .toUriString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Could not list files for reservation " + reservationId, e);
        }
    }


    @Override
    public Stream<Path> loadAll() {
        return Stream.empty();
    }

    @Override
    public Path load(String filename) {
        return null;
    }

    @Override
    public Resource loadAsResource(String filename) {
        return null;
    }

    @Override
    public void delete(String filename) {

    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
}
