package com.xeroxx.backend.controller;

import com.xeroxx.backend.dto.UploadResponse;
import com.xeroxx.backend.service.FileStorageService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final FileStorageService storageService;

    public FileController(FileStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> upload(@RequestPart("file") @NotNull MultipartFile file) {
        UploadResponse response = storageService.store(file);
        return ResponseEntity.ok(response);
    }
}



