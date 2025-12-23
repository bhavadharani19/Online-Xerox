package com.xeroxx.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadResponse {
    private String fileKey;
    private String url;
    private long size;
    private String checksum;
}



