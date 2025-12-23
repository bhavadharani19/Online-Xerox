package com.xeroxx.backend.controller;

import com.xeroxx.backend.dto.*;
import com.xeroxx.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/login/otp/send")
    public ResponseEntity<Void> sendOtp(@RequestParam String identifier) {
        authService.sendOtp(identifier);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login/otp/verify")
    public ResponseEntity<JwtResponse> verifyOtp(@RequestBody @Valid OtpLoginRequest request) {
        return ResponseEntity.ok(authService.loginWithOtp(request));
    }

    @GetMapping("/me")
    public ResponseEntity<String> me(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(user != null ? user.getUsername() : "");
    }
}



