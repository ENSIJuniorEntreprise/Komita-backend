package com.yt.backend.controller;

import com.yt.backend.model.user.User;
import com.yt.backend.repository.UserRepository;
import com.yt.backend.repository.VerificationTokenRepository;
import com.yt.backend.security.VerificationToken;
import com.yt.backend.exception.ValidationException;

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
import java.util.HashMap;
import java.util.regex.Pattern;

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
    
    // Regex pour validation d'email
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    
    // Regex pour validation de mot de passe (au moins 8 caractères, une majuscule, une minuscule, un chiffre)
    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$");

    @Operation(summary = "User registration", description = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest,
            final HttpServletRequest request) {
        
        // Validation personnalisée
        Map<String, String> validationErrors = new HashMap<>();
        
        // Validation de l'email
        if (registerRequest.getEmail() == null || registerRequest.getEmail().isEmpty()) {
            validationErrors.put("email", "L'adresse email est obligatoire");
        } else if (!EMAIL_PATTERN.matcher(registerRequest.getEmail()).matches()) {
            validationErrors.put("email", "Format d'adresse email invalide");
        } else if (userRepository.existsByEmail(registerRequest.getEmail())) {
            validationErrors.put("email", "Cette adresse email est déjà utilisée");
        }
        
        // Validation du mot de passe
        if (registerRequest.getPassword() == null || registerRequest.getPassword().isEmpty()) {
            validationErrors.put("password", "Le mot de passe est obligatoire");
        } else if (!PASSWORD_PATTERN.matcher(registerRequest.getPassword()).matches()) {
            validationErrors.put("password", "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule et un chiffre");
        }
        
        // Validation du prénom
        if (registerRequest.getFirstname() == null || registerRequest.getFirstname().isEmpty()) {
            validationErrors.put("firstname", "Le prénom est obligatoire");
        }
        
        // Validation du nom
        if (registerRequest.getLastname() == null || registerRequest.getLastname().isEmpty()) {
            validationErrors.put("lastname", "Le nom est obligatoire");
        }
        
        // Si des erreurs de validation sont présentes, lancer l'exception
        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Erreurs de validation lors de l'inscription", validationErrors);
        }
        
        return ResponseEntity.ok(service.register(registerRequest, request));
    }

    @Operation(summary = "User authentication", description = "Authenticate a user")
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        // Validation personnalisée
        Map<String, String> validationErrors = new HashMap<>();
        
        // Validation de l'email
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            validationErrors.put("email", "L'adresse email est obligatoire");
        } else if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            validationErrors.put("email", "Format d'adresse email invalide");
        }
        
        // Validation du mot de passe
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            validationErrors.put("password", "Le mot de passe est obligatoire");
        }
        
        // Si des erreurs de validation sont présentes, lancer l'exception
        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Erreurs de validation lors de l'authentification", validationErrors);
        }
        
        try {
            Map<String, String> result = service.authenticate(request);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, String> errors = new HashMap<>();
            errors.put("authentication", e.getMessage());
            throw new ValidationException("Échec de l'authentification", errors);
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            Map<String, String> errors = new HashMap<>();
            errors.put("credentials", "Email ou mot de passe incorrect");
            throw new ValidationException("Identifiants invalides", errors);
        } catch (org.springframework.security.authentication.DisabledException e) {
            Map<String, String> errors = new HashMap<>();
            errors.put("account", "Votre compte est désactivé");
            throw new ValidationException("Compte désactivé", errors);
        } catch (org.springframework.security.authentication.LockedException e) {
            Map<String, String> errors = new HashMap<>();
            errors.put("account", "Votre compte est verrouillé");
            throw new ValidationException("Compte verrouillé", errors);
        } catch (Exception e) {
            Map<String, String> errors = new HashMap<>();
            errors.put("system", "Une erreur est survenue: " + e.getMessage());
            throw new ValidationException("Erreur d'authentification", errors);
        }
    }

    @Operation(summary = "Forgot Password", description = "Request a password reset link")
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        // Validation de l'email
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            errors.put("email", "L'adresse email est obligatoire");
            throw new ValidationException("Email manquant", errors);
        } else if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            Map<String, String> errors = new HashMap<>();
            errors.put("email", "Format d'adresse email invalide");
            throw new ValidationException("Format d'email invalide", errors);
        }
        
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            Map<String, String> errors = new HashMap<>();
            errors.put("email", "Aucun utilisateur trouvé avec cette adresse email");
            throw new ValidationException("Utilisateur non trouvé", errors);
        }

        // Generate reset token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        tokenRepository.save(verificationToken);

        // Send the reset token to user's email
        String resetLink = "https://komita-frontend.onrender.com/reset-password?token=" + token;
        emailService.sendResetPasswordEmail(user.getEmail(), resetLink);

        return ResponseEntity.ok("Un lien de réinitialisation du mot de passe a été envoyé à votre adresse email");
    }

    @Operation(summary = "Reset Password", description = "Reset the user's password")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        // Validation du token
        if (request.getToken() == null || request.getToken().isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            errors.put("token", "Le token est obligatoire");
            throw new ValidationException("Token manquant", errors);
        }
        
        // Validation du nouveau mot de passe
        if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            errors.put("newPassword", "Le nouveau mot de passe est obligatoire");
            throw new ValidationException("Mot de passe manquant", errors);
        } else if (!PASSWORD_PATTERN.matcher(request.getNewPassword()).matches()) {
            Map<String, String> errors = new HashMap<>();
            errors.put("newPassword", "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule et un chiffre");
            throw new ValidationException("Format de mot de passe invalide", errors);
        }
        
        VerificationToken token = tokenRepository.findByToken(request.getToken());
        if (token == null || token.isExpired()) {
            Map<String, String> errors = new HashMap<>();
            errors.put("token", "Token invalide ou expiré");
            throw new ValidationException("Token invalide", errors);
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

        return ResponseEntity.ok("Votre mot de passe a été réinitialisé avec succès");
    }
}
