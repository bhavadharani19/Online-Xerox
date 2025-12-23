package com.xeroxx.backend.service;

import com.xeroxx.backend.dto.*;
import com.xeroxx.backend.entity.User;
import com.xeroxx.backend.repository.UserRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
   
 

    private final NotificationService notificationService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       NotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.notificationService = notificationService;
    }

    @Transactional
    public JwtResponse register(RegisterRequest request) {
        if ((request.getEmail() == null || request.getEmail().isBlank()) &&
                (request.getMobile() == null || request.getMobile().isBlank())) {
            throw new IllegalArgumentException("Email or mobile is required");
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (request.getMobile() != null && userRepository.existsByMobile(request.getMobile())) {
            throw new IllegalArgumentException("Mobile already registered");
        }
        User user = User.builder()
                .email(request.getEmail())
                .mobile(request.getMobile())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();
        userRepository.save(user);
        UserDetails details = toUserDetails(user);
        String token = jwtService.generateToken(details);
        return new JwtResponse(token, Instant.now().plusSeconds(3600));
    }

   public JwtResponse login(LoginRequest request) {
    User user = loadDomainUser(request.getIdentifier());

    if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
        throw new IllegalArgumentException("Invalid credentials");
    }

    String token = jwtService.generateToken(toUserDetails(user));
    return new JwtResponse(token, Instant.now().plusSeconds(3600));
}


    public void sendOtp(String identifier) {
        User user = loadDomainUser(identifier);
        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setOtpCode(otp);
        user.setOtpExpiresAt(Instant.now().plus(10, ChronoUnit.MINUTES));
        userRepository.save(user);
        notificationService.sendOtp(user, otp);
    }

    public JwtResponse loginWithOtp(OtpLoginRequest request) {
        User user = loadDomainUser(request.getIdentifier());
        if (user.getOtpCode() == null || user.getOtpExpiresAt() == null) {
            throw new IllegalArgumentException("OTP not requested");
        }
        if (Instant.now().isAfter(user.getOtpExpiresAt())) {
            throw new IllegalArgumentException("OTP expired");
        }
        if (!request.getOtp().equals(user.getOtpCode())) {
            throw new IllegalArgumentException("Invalid OTP");
        }
        user.setOtpCode(null);
        user.setOtpExpiresAt(null);
        userRepository.save(user);
        String token = jwtService.generateToken(toUserDetails(user));
        return new JwtResponse(token, Instant.now().plusSeconds(3600));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = loadDomainUser(username);
        return toUserDetails(user);
    }

    private User loadDomainUser(String identifier) {
        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByMobile(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private UserDetails toUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail() != null ? user.getEmail() : user.getMobile(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}



