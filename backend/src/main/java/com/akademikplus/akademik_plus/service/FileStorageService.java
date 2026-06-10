package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.exception.FileStorageException;
import com.akademikplus.akademik_plus.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );
    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB

    private final Path uploadDir;

    public FileStorageService(@Value("${file.upload-dir:uploads}") String uploadDir) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new FileStorageException("Could not create upload directory: " + e.getMessage());
        }
    }

    public String store(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("File must not be empty.");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new ValidationException("File size exceeds the 5 MB limit.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new ValidationException("Only JPEG, PNG, WEBP and GIF images are allowed.");
        }

        String extension = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + extension;

        Path targetDir = uploadDir.resolve(subDir).normalize();
        try {
            Files.createDirectories(targetDir);
            Files.copy(file.getInputStream(), targetDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException("Could not save file: " + e.getMessage());
        }

        String url = "/uploads/" + subDir + "/" + filename;
        log.info("File stored: {}", url);
        return url;
    }

    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;
        String relative = fileUrl.replaceFirst("^/uploads/", "");
        Path filePath = uploadDir.resolve(relative).normalize();
        try {
            Files.deleteIfExists(filePath);
            log.info("File deleted: {}", fileUrl);
        } catch (IOException e) {
            log.warn("Could not delete file {}: {}", fileUrl, e.getMessage());
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
