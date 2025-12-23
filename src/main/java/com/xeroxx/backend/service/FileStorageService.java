package com.xeroxx.backend.service;

import com.xeroxx.backend.dto.UploadResponse;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);
    private static final Set<String> ALLOWED_EXT = Set.of("pdf", "doc", "docx", "png", "jpg", "jpeg");

    @Value("${storage.local-path:temp-uploads}")
    private String localPath;

    @Value("${storage.s3.enabled:false}")
    private boolean s3Enabled;

    @Value("${storage.s3.bucket:}")
    private String s3Bucket;

    @Value("${storage.s3.region:ap-south-1}")
    private String s3Region;

    public UploadResponse store(MultipartFile file) {
        validateFile(file);
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String key = UUID.randomUUID() + "." + extension;
        try {
            byte[] content = file.getBytes();
            String checksum = checksum(content);
            if (s3Enabled && !s3Bucket.isBlank()) {
                uploadToS3(key, content, file.getContentType());
                String url = "https://" + s3Bucket + ".s3." + s3Region + ".amazonaws.com/" + key;
                return new UploadResponse(key, url, content.length, checksum);
            }
            Path dir = Path.of(localPath);
            Files.createDirectories(dir);
            Path destination = dir.resolve(key);
            Files.write(destination, content);
            return new UploadResponse(key, destination.toAbsolutePath().toString(), content.length, checksum);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store file", e);
        }
    }

    private void uploadToS3(String key, byte[] content, String contentType) {
        S3Client s3 = S3Client.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.of(s3Region))
                .build();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(s3Bucket)
                .key(key)
                .contentType(contentType)
                .build();
        s3.putObject(request, RequestBody.fromBytes(content));
        log.info("Uploaded file {} to bucket {}", key, s3Bucket);
    }

    private String checksum(byte[] content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(content));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Checksum failed", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
        if (!ALLOWED_EXT.contains(extension)) {
            throw new IllegalArgumentException("Unsupported file type");
        }
        if (file.getSize() > 15 * 1024 * 1024) {
            throw new IllegalArgumentException("File too large (15MB max)");
        }
    }
}



