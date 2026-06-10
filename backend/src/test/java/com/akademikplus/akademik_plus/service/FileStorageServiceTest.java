package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService(tempDir.toString());
    }

    @Test
    void store_savesFileAndReturnsUrl() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "fake-image-content".getBytes());

        String url = fileStorageService.store(file, "users");

        assertThat(url).startsWith("/uploads/users/");
        assertThat(url).endsWith(".jpg");
    }

    @Test
    void store_throwsValidation_whenFileEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.jpg", "image/jpeg", new byte[0]);

        assertThatThrownBy(() -> fileStorageService.store(emptyFile, "users"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("not be empty");
    }

    @Test
    void store_throwsValidation_whenFileTooLarge() {
        byte[] largeContent = new byte[5 * 1024 * 1024 + 1];
        MockMultipartFile largeFile = new MockMultipartFile(
                "file", "large.jpg", "image/jpeg", largeContent);

        assertThatThrownBy(() -> fileStorageService.store(largeFile, "users"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("5 MB limit");
    }

    @Test
    void store_throwsValidation_whenContentTypeNotAllowed() {
        MockMultipartFile pdfFile = new MockMultipartFile(
                "file", "document.pdf", "application/pdf", "pdf content".getBytes());

        assertThatThrownBy(() -> fileStorageService.store(pdfFile, "users"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Only JPEG, PNG, WEBP and GIF");
    }

    @Test
    void store_supportsAllowedContentTypes() {
        for (String contentType : new String[]{"image/jpeg", "image/png", "image/webp", "image/gif"}) {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "image.jpg", contentType, "content".getBytes());

            String url = fileStorageService.store(file, "test");

            assertThat(url).startsWith("/uploads/test/");
        }
    }

    @Test
    void delete_removesFile() throws IOException {
        Path subDir = tempDir.resolve("users");
        Files.createDirectories(subDir);
        Path testFile = subDir.resolve("test.jpg");
        Files.writeString(testFile, "test content");

        fileStorageService.delete("/uploads/users/test.jpg");

        assertThat(testFile).doesNotExist();
    }

    @Test
    void delete_doesNothingForNullUrl() {
        fileStorageService.delete(null);
        // No exception
    }

    @Test
    void delete_doesNothingForBlankUrl() {
        fileStorageService.delete("   ");
        // No exception
    }

    @Test
    void delete_doesNothingForNonExistentFile() {
        fileStorageService.delete("/uploads/users/nonexistent.jpg");
        // No exception
    }
}
