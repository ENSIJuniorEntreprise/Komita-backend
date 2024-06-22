package com.yt.backend.auth;

import com.yt.backend.model.user.User;
import com.yt.backend.repository.UserRepository;
import com.yt.backend.repository.VerificationTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Operation(summary = "User registration", description = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<?> register( @Valid
            @RequestBody RegisterRequest registerRequest,
            final HttpServletRequest request
    ) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("email address already exists");
        }
        return ResponseEntity.ok(service.register(registerRequest, request));
    }

    @Operation(summary = "User authentication", description = "Authenticate a user")
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        // Check if the user exists and their status is true
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        } else if (!user.isStatus()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User account is not active. Please verify your email first.");
        }

        // Authenticate user if status is true
        return ResponseEntity.ok(service.authenticate(request));
    }
}
