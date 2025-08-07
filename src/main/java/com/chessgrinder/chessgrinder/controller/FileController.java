package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.entities.FileEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.repositories.FileRepository;
import com.chessgrinder.chessgrinder.security.AuthenticatedUserArgumentResolver.AuthenticatedUser;
import com.chessgrinder.chessgrinder.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * Controller for file upload and download operations.
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileStorageService fileStorageService;
    private final FileRepository fileRepository;

    /**
     * Upload a file.
     *
     * @param file The file to upload
     * @param purpose The purpose of the file (e.g., "tournament_image", "profile_image")
     * @param authenticatedUser The authenticated user
     * @return The uploaded file entity
     * @throws IOException If an I/O error occurs
     */
    @PostMapping
    public FileEntity uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "purpose", required = false) String purpose,
            @AuthenticatedUser UserEntity authenticatedUser
    ) throws IOException {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }
        
        return fileStorageService.storeFile(file, authenticatedUser.getId(), purpose);
    }

    /**
     * Get a file by ID.
     *
     * @param fileId The ID of the file to retrieve
     * @return The file as a response entity
     * @throws IOException If an I/O error occurs
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable UUID fileId) throws IOException {
        FileEntity fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));
        
        InputStream fileStream = fileStorageService.getFile(fileId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(fileEntity.getContentType()));
        headers.setContentDispositionFormData("attachment", fileEntity.getOriginalFileName());
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(fileStream));
    }

    /**
     * Delete a file by ID.
     *
     * @param fileId The ID of the file to delete
     * @param authenticatedUser The authenticated user
     * @throws IOException If an I/O error occurs
     */
    @DeleteMapping("/{fileId}")
    public void deleteFile(
            @PathVariable UUID fileId,
            @AuthenticatedUser UserEntity authenticatedUser
    ) throws IOException {
        FileEntity fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));
        
        if (fileEntity.getUser() != null && !fileEntity.getUser().getId().equals(authenticatedUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to delete this file");
        }
        
        fileStorageService.deleteFile(fileId);
    }

    /**
     * Get files by user ID and purpose.
     *
     * @param userId The ID of the user
     * @param purpose The purpose of the files
     * @return A list of file entities
     */
    @GetMapping("/user/{userId}")
    public List<FileEntity> getFilesByUserAndPurpose(
            @PathVariable UUID userId,
            @RequestParam(value = "purpose", required = false) String purpose
    ) {
        if (purpose != null) {
            return fileRepository.findByUserIdAndPurpose(userId, purpose);
        } else {
            return fileRepository.findByUserId(userId);
        }
    }
}