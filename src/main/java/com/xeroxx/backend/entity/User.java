package com.xeroxx.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String mobile;

    @NotBlank
    private String passwordHash;

    private String role;

    private String otpCode;

    private Instant otpExpiresAt;

    private Instant createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
        if (this.role == null) {
            this.role = "USER";
        }
    }
}



