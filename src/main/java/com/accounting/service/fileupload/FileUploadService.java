package com.accounting.service.fileupload;


import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface FileUploadService {

    String storeFile(MultipartFile file, Long reservationId);

    Path getFilePath(String folder, String filename);

    List<String> getAllDownloadUrlsForReservation(Long reservationId);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void delete(String filename);

    void deleteAll();
}
