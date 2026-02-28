package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.entities.FileEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Service interface for handling file storage operations.
 * This interface defines methods for storing, retrieving, and deleting files.
 */
public interface FileStorageService {

    /**
     * Store a file in the storage system.
     *
     * @param file The file to store
     * @param userId The ID of the user who is uploading the file
     * @param purpose The purpose of the file (e.g., "tournament_image", "profile_image")
     * @return The stored file entity
     * @throws IOException If an I/O error occurs
     */
    FileEntity storeFile(MultipartFile file, UUID userId, String purpose) throws IOException;

    /**
     * Get a file from the storage system.
     *
     * @param fileId The ID of the file to retrieve
     * @return An input stream containing the file data
     * @throws IOException If an I/O error occurs
     */
    InputStream getFile(UUID fileId) throws IOException;

    /**
     * Delete a file from the storage system.
     *
     * @param fileId The ID of the file to delete
     * @throws IOException If an I/O error occurs
     */
    void deleteFile(UUID fileId) throws IOException;

    /**
     * Get the URL for accessing a file.
     *
     * @param fileId The ID of the file
     * @return The URL for accessing the file
     */
    String getFileUrl(UUID fileId);
}