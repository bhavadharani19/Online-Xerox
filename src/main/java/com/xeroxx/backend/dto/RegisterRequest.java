package com.xeroxx.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String name;
    private String email;
    private String password;

    // AuthService expects getMobile()
    private String mobile;

    // Optional fields used in your AuthService
    private String role;
    private Long shopId;
}
