package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.configuration.FileStorageProperties;
import com.chessgrinder.chessgrinder.entities.FileEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.repositories.FileRepository;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

/**
 * Implementation of FileStorageService that stores files in an S3-compatible storage system.
 * This is a placeholder implementation that logs a message indicating that S3 storage is not yet implemented.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class S3FileStorageService implements FileStorageService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final FileStorageProperties fileStorageProperties;

    @Override
    public FileEntity storeFile(MultipartFile file, UUID userId, String purpose) throws IOException {
        log.warn("S3 storage is not yet fully implemented. Using a placeholder implementation.");

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String contentType = file.getContentType();
        long size = file.getSize();

        // Generate a unique file name to prevent collisions
        String fileName = UUID.randomUUID() + "_" + originalFilename;

        // Save the file metadata to the database
        UserEntity user = userRepository.findById(userId).orElse(null);

        FileEntity fileEntity = FileEntity.builder()
                .fileName(fileName)
                .originalFileName(originalFilename)
                .filePath("s3://" + fileStorageProperties.getS3().getBucket() + "/" + fileName)
                .contentType(contentType)
                .size(size)
                .storageType("s3")
                .user(user)
                .purpose(purpose)
                .build();

        return fileRepository.save(fileEntity);
    }

    @Override
    public InputStream getFile(UUID fileId) throws IOException {
        log.warn("S3 storage is not yet fully implemented. Using a placeholder implementation.");

        FileEntity fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new IOException("File not found with ID: " + fileId));

        // Return an empty stream as a placeholder
        return new ByteArrayInputStream(new byte[0]);
    }

    @Override
    public void deleteFile(UUID fileId) throws IOException {
        log.warn("S3 storage is not yet fully implemented. Using a placeholder implementation.");

        FileEntity fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new IOException("File not found with ID: " + fileId));

        fileRepository.delete(fileEntity);
    }

    @Override
    public String getFileUrl(UUID fileId) {
        log.warn("S3 storage is not yet fully implemented. Using a placeholder implementation.");

        FileEntity fileEntity = fileRepository.findById(fileId).orElse(null);
        if (fileEntity == null) {
            return null;
        }

        FileStorageProperties.S3 s3Properties = fileStorageProperties.getS3();
        String endpoint = s3Properties.getEndpoint();
        String bucket = s3Properties.getBucket();

        if (endpoint != null && !endpoint.isEmpty()) {
            // For custom S3 endpoints like MinIO
            return endpoint + "/" + bucket + "/" + fileEntity.getFileName();
        } else {
            // For AWS S3
            String region = s3Properties.getRegion();
            return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + fileEntity.getFileName();
        }
    }
}
