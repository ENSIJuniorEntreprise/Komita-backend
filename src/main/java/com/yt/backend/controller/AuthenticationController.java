package com.yt.backend.controller;

import com.yt.backend.model.user.User;
import com.yt.backend.repository.UserRepository;
import com.yt.backend.repository.VerificationTokenRepository;
import com.yt.backend.security.VerificationToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import com.yt.backend.service.AuthenticationService;
import com.yt.backend.service.EmailService;
import com.yt.backend.dto.AuthenticationRequest;
import com.yt.backend.dto.ForgotPasswordRequest;
import com.yt.backend.dto.RegisterRequest;
import com.yt.backend.dto.ResetPasswordRequest;

import java.io.IOException;
import java.util.UUID;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "User registration", description = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest,
            final HttpServletRequest request) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("email address already exists");
        }
        return ResponseEntity.ok(service.register(registerRequest, request));
    }

    @Operation(summary = "User authentication", description = "Authenticate a user")
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            Map<String, String> result = service.authenticate(request);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An error occurred during authentication"));
        }
    }

    @Operation(summary = "Forgot Password", description = "Request a password reset link")
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Generate reset token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        tokenRepository.save(verificationToken);

        // Send the reset token to user's email
        String resetLink = "https://komita-frontend.onrender.com/reset-password?token=" + token;
        emailService.sendResetPasswordEmail(user.getEmail(), resetLink);

        return ResponseEntity.ok("Password reset link sent to your email");
    }

    @Operation(summary = "Reset Password", description = "Reset the user's password")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        VerificationToken token = tokenRepository.findByToken(request.getToken());
        if (token == null || token.isExpired()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
        }

        User user = token.getUser();

        service.revokeAllUserTokens(user);

        // Use PasswordEncoder to encode the new password
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());

        // Set the encoded password for the user
        user.setPassword(encodedPassword);
        userRepository.save(user);

        // Delete the token after use
        tokenRepository.delete(token);

        return ResponseEntity.ok("Password has been reset successfully");
    }


}
