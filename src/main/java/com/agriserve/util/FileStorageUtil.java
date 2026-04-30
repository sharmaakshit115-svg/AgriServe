package com.agriserve.util;

import com.agriserve.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Utility for storing uploaded files on the local filesystem.
 * In production, replace with S3/GCS/Azure Blob Storage.
 */
@Slf4j
@Component
public class FileStorageUtil {

    @Value("${app.file.upload-dir:./uploads}")
    private String uploadDir;

    /**
     * Stores the file in a sub-directory named after the prefix (e.g., farmer_42)
     * and returns the absolute URI to the stored file.
     */
    public String storeFile(MultipartFile file, String prefix) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Cannot store an empty file");
        }

        // Sanitise the original filename
        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown"
        );

        // Generate a unique filename to prevent collisions
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex);
        }
        String storedFilename = prefix + "_" + UUID.randomUUID() + extension;

        try {
            Path targetDir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(targetDir);

            Path targetPath = targetDir.resolve(storedFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.debug("Stored file {} at {}", originalFilename, targetPath);
            return targetPath.toString();
        } catch (IOException ex) {
            throw new BusinessException("Failed to store file: " + ex.getMessage());
        }
    }
}
