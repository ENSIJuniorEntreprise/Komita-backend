package com.yt.backend.controller;


import com.yt.backend.model.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.yt.backend.model.Consultation;
import com.yt.backend.service.ConsultationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth/consultations")
public class ConsultationController {
    private final ConsultationService consultationService;

    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    /**
     * Consult a service.
     *
     * @param serviceId The ID of the service to consult.
     * @param user      The user requesting the consultation.
     * @return ResponseEntity containing the created consultation.
     */

    @Operation(summary = "Consult a service", description = "Consult a service by providing its ID and the requesting user")
    @PostMapping("/{serviceId}/consult")
    public ResponseEntity<Consultation> consultService(@PathVariable Long serviceId, @RequestBody User user) {
        Consultation consultation = consultationService.createConsultation(serviceId, user);
        return ResponseEntity.ok(consultation);
    }

    /**
     * Get all consultations.
     *
     * @return List of all consultations.
     */
    @Operation(summary = "Get all consultations", description = "Retrieve a list of all consultations")
    @GetMapping("/getAll")
    public List<Consultation> getAllConsultations() {
        return consultationService.getAllConsultations();
    }

    /**
     * Get a consultation by ID.
     *
     * @param id The ID of the consultation to retrieve.
     * @return ResponseEntity containing the consultation, or 404 if not found.
     */

    @Operation(summary = "Get a consultation by ID", description = "Retrieve a consultation by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<Consultation> getConsultationById(@PathVariable Long id) {
        Consultation consultation = consultationService.getConsultationById(id);
        if (consultation != null) {
            return ResponseEntity.ok(consultation);
        } else {
            return ResponseEntity.notFound().build();
        }

        /**
         * Delete a consultation by ID.
         *
         * @param id The ID of the consultation to delete.
         * @return ResponseEntity indicating success or failure of deletion.
         */
    }

    @Operation(summary = "Delete a consultation by ID", description = "Delete a consultation by providing its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsultation(@PathVariable Long id) {
        consultationService.deleteConsultation(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get consultations by user", description = "Retrieve consultations associated with a user by providing the user ID")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Consultation> getConsultationsByUser(@PathVariable Long userId) {
        Consultation consultations = consultationService.getConsultationsByUser(userId);
        if (consultations != null) {
            return ResponseEntity.ok(consultations);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get consultations by service", description = "Retrieve consultations associated with a service by providing the service ID")
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<Consultation> getConsultationsByService(@PathVariable Long serviceId) {
        Consultation consultations = consultationService.getConsultationsByService(serviceId);
        if (consultations != null) {
            return ResponseEntity.ok(consultations);
        } else {
            return ResponseEntity.notFound().build();
}
}
}
