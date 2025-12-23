package com.xeroxx.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpLoginRequest {
    @NotBlank
    private String identifier;

    @NotBlank
    private String otp;
}



