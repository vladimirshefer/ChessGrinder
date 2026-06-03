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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

/**
 * Implementation of FileStorageService that stores files on the local file system.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LocalFileStorageService implements FileStorageService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final FileStorageProperties fileStorageProperties;

    @Override
    public FileEntity storeFile(MultipartFile file, UUID userId, String purpose) throws IOException {
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String contentType = file.getContentType();
        long size = file.getSize();
        
        // Generate a unique file name to prevent collisions
        String fileName = UUID.randomUUID() + "_" + originalFilename;
        
        // Create the directory if it doesn't exist
        Path uploadDir = Paths.get(fileStorageProperties.getLocal().getDirectory());
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        
        // Save the file to the file system
        Path filePath = uploadDir.resolve(fileName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        
        // Save the file metadata to the database
        UserEntity user = userRepository.findById(userId).orElse(null);
        
        FileEntity fileEntity = FileEntity.builder()
                .fileName(fileName)
                .originalFileName(originalFilename)
                .filePath(filePath.toString())
                .contentType(contentType)
                .size(size)
                .storageType("local")
                .user(user)
                .purpose(purpose)
                .build();
        
        return fileRepository.save(fileEntity);
    }

    @Override
    public InputStream getFile(UUID fileId) throws IOException {
        FileEntity fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new IOException("File not found with ID: " + fileId));
        
        Path filePath = Paths.get(fileEntity.getFilePath());
        return Files.newInputStream(filePath);
    }

    @Override
    public void deleteFile(UUID fileId) throws IOException {
        FileEntity fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new IOException("File not found with ID: " + fileId));
        
        Path filePath = Paths.get(fileEntity.getFilePath());
        Files.deleteIfExists(filePath);
        
        fileRepository.delete(fileEntity);
    }

    @Override
    public String getFileUrl(UUID fileId) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/")
                .path(fileId.toString())
                .toUriString();
    }
}