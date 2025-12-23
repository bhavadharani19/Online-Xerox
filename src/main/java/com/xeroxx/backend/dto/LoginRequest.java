package com.xeroxx.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String identifier; // email or mobile

    @NotBlank
    private String password;
}



