package com.codewithpcodes.cardiag.storage;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    @Value("${application.file.uploads.media-output-path}")
    private String fileUploadPath;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;


    public String saveProfilePicture(
            @NonNull MultipartFile sourceFile,
            @NonNull UUID userId
    ) {
        validateFile(sourceFile);
        final String subPath = "users/" + userId;
        String filePath = writeFile(sourceFile, subPath);
        // Convert filesystem path to public URL (strip leading ./)
        return "/" + filePath.replaceAll("^\\./", "").replace('\\', '/');
    }

    public byte[] readFile(@NonNull String storedFilePath) throws IOException {
        Path path = resolveAndValidatePath(storedFilePath);
        if (!Files.exists(path)) return null;
        return Files.readAllBytes(path);
    }

    public boolean deleteFile(@NonNull String storedFilePath) {
        Path targetPath = resolveAndValidatePath(storedFilePath);
        try {
            boolean deleted = Files.deleteIfExists(targetPath);
            if (deleted) log.info("Deleted file {}", targetPath);
            else log.warn("File not found for deletion: {}", targetPath);
            return deleted;
        } catch (IOException e) {
            log.error("Failed to delete file {}: {}", targetPath, e.getMessage());
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    private String writeFile(MultipartFile sourceFile, String subPath) {
        final String dir = fileUploadPath + File.separator + subPath;
        File targetFolder = new File(dir);
        if (!targetFolder.exists() && !targetFolder.mkdirs()) {
            log.warn("Failed to create upload directory: {}", dir);
            throw new RuntimeException("Failed to create upload directory");
        }

        String ext = getFileExtension(sourceFile.getOriginalFilename());
        String filename = UUID.randomUUID() + ext;
        String filePath = dir + File.separator + filename;
        Path target = Paths.get(filePath);

        try {
            Files.write(target, sourceFile.getBytes());
            log.info("Saved file {} → {}", sourceFile.getOriginalFilename(), target);
            return filePath;
        } catch (IOException e) {
            log.error("Failed to write file: {}", e.getMessage());
            throw new RuntimeException("Failed to save file", e);
        }
    }

    private Path resolveAndValidatePath(String storedFilePath) {
        Path base = Paths.get(fileUploadPath).toAbsolutePath().normalize();
        Path target = Paths.get(storedFilePath).toAbsolutePath().normalize();
        if (!target.startsWith(base)) {
            log.warn("Path traversal attempt blocked: {}", storedFilePath);
            throw new IllegalArgumentException("Illegal file path");
        }
        return target;
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty())
            throw new RuntimeException("File is empty");
        if (file.getSize() > MAX_FILE_SIZE)
            throw new RuntimeException(
                    "File too large. Maximum size is " + (MAX_FILE_SIZE / 1024 / 1024) + " MB");
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) return "";
        int dot = fileName.lastIndexOf('.');
        return dot == -1 ? "" : "." + fileName.substring(dot + 1).toLowerCase();
    }
}
