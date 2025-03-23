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

@RestController
@RequestMapping("/api/v1/consultations")
@Validated
public class ConsultationController {
    private final ConsultationService consultationService;

    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
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
            @Valid @RequestBody User user) {
        if (user == null) {
            throw new BusinessException("User data is required");
        }
        Consultation consultation = consultationService.createConsultation(serviceId, user);
        return ResponseEntity.ok(consultation);
    }

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
