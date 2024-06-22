package com.yt.backend.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Operation(summary = "Validate email verification token", description = "Validate the email verification token sent during registration")
    @GetMapping("/register/verifyEmail")
    public ResponseEntity<?> validateEmail(@RequestParam("token") String verificationToken) {
        String result = authenticationService.validateToken(verificationToken);
        if ("valid".equals(result)) {
            // Token is valid, so user status has been successfully updated
            return ResponseEntity.ok("Your email verification token is valid.");
        } else {
            // Token is invalid or expired
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired email verification token.");
        }
    }

    @Operation(summary = "Verify email", description = "Verify the email using the verification token")
    @PostMapping("/register/verifyEmail")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String verificationToken) {
        String result = authenticationService.validateToken(verificationToken);
        if ("valid".equals(result)) {
            // Token is valid, so user status has been successfully updated
            return ResponseEntity.ok("Your account has been successfully verified.");
        } else {
            // Token is invalid or expired
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to verify account. Please check your link or try again later.");
        }
    }
}
