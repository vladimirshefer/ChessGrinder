package com.chessgrinder.chessgrinder.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for file storage.
 */
@Configuration
@ConfigurationProperties(prefix = "chessgrinder.file-storage")
@Data
public class FileStorageProperties {

    /**
     * The type of storage to use (local or s3).
     */
    private String type = "local";

    /**
     * Configuration for local file storage.
     */
    private Local local = new Local();

    /**
     * Configuration for S3 file storage.
     */
    private S3 s3 = new S3();

    /**
     * Configuration properties for local file storage.
     */
    @Data
    public static class Local {
        /**
         * The directory where files will be stored.
         */
        private String directory = "./files";
    }

    /**
     * Configuration properties for S3 file storage.
     */
    @Data
    public static class S3 {
        /**
         * The endpoint URL for the S3 service.
         */
        private String endpoint;

        /**
         * The region for the S3 service.
         */
        private String region = "us-east-1";

        /**
         * The bucket name for storing files.
         */
        private String bucket;

        /**
         * The access key for the S3 service.
         */
        private String accessKey;

        /**
         * The secret key for the S3 service.
         */
        private String secretKey;
    }
}
