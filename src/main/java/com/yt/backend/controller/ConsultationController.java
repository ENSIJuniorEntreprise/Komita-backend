package com.yt.backend.controller;

import com.yt.backend.model.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.yt.backend.model.Consultation;
import com.yt.backend.service.ConsultationService;
import com.yt.backend.exception.ResourceNotFoundException;
import com.yt.backend.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

import java.util.List;

import com.yt.backend.service.UserService;
import com.yt.backend.repository.UserRepository;

@RestController
@RequestMapping("/api/v1/consultations")
@Validated
public class ConsultationController {
    private final ConsultationService consultationService;
    private final UserService userService;
    private final UserRepository userRepository;

    public ConsultationController(ConsultationService consultationService, UserService userService, UserRepository userRepository) {
        this.consultationService = consultationService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Consult a service")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consultation created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Service not found")
    })
    @PostMapping("/{serviceId}/consult")
    public ResponseEntity<Consultation> consultService(
            @PathVariable Long serviceId,
            @Valid @RequestBody(required = false) User user,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        System.out.println("Controller received user from body: " + (user != null ? user.getId() + ", " + user.getEmail() : "null"));
        System.out.println("Auth header: " + (authHeader != null ? authHeader : "none"));
        
        // Si nous avons un header d'autorisation, essayons de récupérer l'utilisateur authentifié
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                // Extrayez l'email de l'utilisateur du token JWT
                String token = authHeader.substring(7); // Supprime "Bearer "
                String email = extractEmailFromToken(token);
                
                if (email != null && !email.isEmpty()) {
                    System.out.println("Found email in token: " + email);
                    
                    // Recherchez l'utilisateur par email en utilisant le repository
                    User authenticatedUser = userRepository.findByEmail(email);
                    
                    if (authenticatedUser != null) {
                        System.out.println("Found authenticated user: " + authenticatedUser.getId());
                        return ResponseEntity.ok(consultationService.createConsultation(serviceId, authenticatedUser.getId()));
                    }
                }
            } catch (Exception e) {
                System.out.println("Error processing JWT token: " + e.getMessage());
            }
        }
        
        // Si nous n'avons pas pu récupérer l'utilisateur authentifié, utilisez l'utilisateur du corps ou anonyme
        if (user != null && user.getId() != null && user.getId() > 0) {
            return ResponseEntity.ok(consultationService.createConsultation(serviceId, user.getId()));
        } else if (user != null && !isIncompleteUser(user)) {
            return ResponseEntity.ok(consultationService.createConsultation(serviceId, user));
        } else {
            // Utilisez l'utilisateur anonyme
            return ResponseEntity.ok(consultationService.createConsultation(serviceId, (User) null));
        }
    }
    
    // Méthode pour extraire l'email du token JWT
    private String extractEmailFromToken(String token) {
        try {
            System.out.println("Attempting to extract email from token: " + token);
            
            // Exemple simplifié pour décoder un JWT sans vérification de signature
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                System.out.println("Invalid token format: expected 3 parts but got " + parts.length);
                return null;
            }
            
            // Décodez la partie payload (deuxième partie)
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            System.out.println("Decoded payload: " + payload);
            
            // Votre token JWT utilise "sub" pour l'email
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"sub\"\\s*:\\s*\"([^\"]+)\"");
            java.util.regex.Matcher matcher = pattern.matcher(payload);
            
            if (matcher.find()) {
                String sub = matcher.group(1);
                System.out.println("Found sub: " + sub);
                return sub;
            }
            
            // Si "sub" n'est pas trouvé, essayez avec "email"
            pattern = java.util.regex.Pattern.compile("\"email\"\\s*:\\s*\"([^\"]+)\"");
            matcher = pattern.matcher(payload);
            
            if (matcher.find()) {
                String email = matcher.group(1);
                System.out.println("Found email: " + email);
                return email;
            }
            
            System.out.println("No email or sub found in token");
            return null;
        } catch (Exception e) {
            System.out.println("Error extracting email from token: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    // Helper method to check if a user has incomplete information
    private boolean isIncompleteUser(User user) {
        return user.getEmail() == null || user.getEmail().isEmpty() ||
               user.getFirstname() == null || user.getFirstname().isEmpty() ||
               user.getLastname() == null || user.getLastname().isEmpty();
    }
    
    // @Operation(summary = "Consult a service anonymously")
    // @ApiResponses(value = {
    //     @ApiResponse(responseCode = "200", description = "Anonymous consultation created successfully"),
    //     @ApiResponse(responseCode = "404", description = "Service not found")
    // })
    // @PostMapping("/{serviceId}/consult-anonymous")
    // public ResponseEntity<Consultation> consultServiceAnonymously(@PathVariable Long serviceId) {
    //     // Pass null as user to trigger anonymous user creation/retrieval
    //     Consultation consultation = consultationService.createConsultation(serviceId, (User) null);
    //     return ResponseEntity.ok(consultation);
    // }

    @Operation(summary = "Get all consultations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consultations retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No consultations found")
    })
    @GetMapping
    public ResponseEntity<List<Consultation>> getAllConsultations() {
        List<Consultation> consultations = consultationService.getAllConsultations();
        return ResponseEntity.ok(consultations);
    }

    @Operation(summary = "Get consultation by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consultation retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Consultation not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Consultation> getConsultationById(@PathVariable Long id) {
        Consultation consultation = consultationService.getConsultationById(id);
        return ResponseEntity.ok(consultation);
    }

    @Operation(summary = "Delete consultation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Consultation deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Consultation not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsultation(@PathVariable Long id) {
        consultationService.deleteConsultation(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get consultations by user ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consultations retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No consultations found for user")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Consultation>> getConsultationsByUser(@PathVariable Long userId) {
        List<Consultation> consultations = consultationService.getConsultationsByUser(userId);
        return ResponseEntity.ok(consultations);
    }

    @Operation(summary = "Get consultations by service ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consultations retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No consultations found for service")
    })
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<Consultation>> getConsultationsByService(@PathVariable Long serviceId) {
        List<Consultation> consultations = consultationService.getConsultationsByService(serviceId);
        return ResponseEntity.ok(consultations);
    }
}
