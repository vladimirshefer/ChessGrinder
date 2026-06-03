package com.chessgrinder.chessgrinder.configuration;

import com.chessgrinder.chessgrinder.service.FileStorageService;
import com.chessgrinder.chessgrinder.service.LocalFileStorageService;
import com.chessgrinder.chessgrinder.service.S3FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for file storage.
 * Selects the appropriate FileStorageService implementation based on the configuration properties.
 */
@Configuration
@RequiredArgsConstructor
public class FileStorageConfig {

    private final FileStorageProperties fileStorageProperties;
    private final LocalFileStorageService localFileStorageService;
    private final S3FileStorageService s3FileStorageService;

    /**
     * Creates a FileStorageService bean based on the configuration properties.
     *
     * @return The appropriate FileStorageService implementation
     */
    @Bean
    @Primary
    public FileStorageService fileStorageService() {
        String type = fileStorageProperties.getType();
        
        if ("s3".equalsIgnoreCase(type)) {
            return s3FileStorageService;
        } else {
            return localFileStorageService;
        }
    }
}