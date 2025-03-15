package com.yt.backend.controller;

import com.yt.backend.model.Keyword;
import com.yt.backend.repository.KeywordRepository;
import com.yt.backend.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.codehaus.plexus.resource.loader.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.yt.backend.repository.ServiceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RequestMapping("/api/v1")
@RestController
public class KeywordController {

    private final ServiceRepository serviceRepository;
    private final KeywordRepository keywordRepository;
    private final ServiceService serviceService;
    public static final String MESSAGE = "Not found Service with id = ";

    public KeywordController(ServiceRepository serviceRepository, KeywordRepository keywordRepository, ServiceService serviceService) {
        this.serviceRepository = serviceRepository;
        this.keywordRepository = keywordRepository;
        this.serviceService = serviceService;
    }
    @Operation(summary = "Get all keywords by service ID",
            description = "Retrieves all keywords associated with a service by its ID")
    @ApiResponse(responseCode = "200", description = "List of keywords retrieved successfully",
            content = @Content(schema = @Schema(implementation = Keyword.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/services/{serviceId}/keywords")
    public ResponseEntity<List<Keyword>> getAllKeywordsByServiceId(@PathVariable(value = "serviceId") Long serviceId, Authentication authentication) throws ResourceNotFoundException {
        boolean role = serviceService.isAdminOrProfessional(authentication);
        if (role) {
            if (!serviceRepository.existsById(serviceId)) {
                throw new ResourceNotFoundException(MESSAGE + serviceId);
            }

            List<Keyword> keywords = keywordRepository.findKeywordByServiceId(serviceId);
            return new ResponseEntity<>(keywords, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "Get keyword by ID",
            description = "Retrieves a keyword by its ID")
    @ApiResponse(responseCode = "200", description = "Keyword retrieved successfully",
            content = @Content(schema = @Schema(implementation = Keyword.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/keywords/{id}")
    public ResponseEntity<Keyword> getKeywordsByServiceId(@PathVariable(value = "id") Long id, Authentication authentication) throws ResourceNotFoundException {
        boolean role = serviceService.isAdminOrProfessional(authentication);
        if (role) {
            Keyword keyword = keywordRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Not found Keyword with id = " + id));

            return new ResponseEntity<>(keyword, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }


    @Operation(summary = "Create a keyword",
            description = "Allows authorized users to create a new keyword associated with a service")
    @ApiResponse(responseCode = "201", description = "Keyword created successfully",
            content = @Content(schema = @Schema(implementation = Keyword.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping("/services/{serviceId}/keywords/createKeyword")
    public ResponseEntity<Keyword> createKeyword(@PathVariable(value = "serviceId") Long serviceId,
                                                 @RequestBody Keyword keywordRequest, Authentication authentication) throws ResourceNotFoundException {
        boolean role = serviceService.isAdminOrProfessional(authentication);
        if (!role) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Keyword keyword = serviceRepository.findById(serviceId)
                .map(service -> {
                    keywordRequest.setService(service);
                    return keywordRepository.save(keywordRequest);
                })
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE + serviceId));

        return new ResponseEntity<>(keyword, HttpStatus.CREATED);
    }


    @Operation(summary = "Update a keyword",
            description = "Allows authorized users to update an existing keyword")
    @ApiResponse(responseCode = "200", description = "Keyword updated successfully",
            content = @Content(schema = @Schema(implementation = Keyword.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PutMapping("/keywords/updateKeyword/{id}")
    public ResponseEntity<Keyword> updateKeyword(@PathVariable("id") long id, @RequestBody Keyword keywordRequest, Authentication authentication) throws ResourceNotFoundException {
        boolean role = serviceService.isAdminOrProfessional(authentication);
        if (role) {
            Keyword keyword = keywordRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("KeywordId " + id + "not found"));

            keyword.setKeywordName(keywordRequest.getKeywordName());

            return new ResponseEntity<>(keywordRepository.save(keyword), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

    }


    @Operation(summary = "Delete a keyword",
            description = "Allows authorized users to delete a keyword")
    @ApiResponse(responseCode = "204", description = "Keyword deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @DeleteMapping("/keywords/delete/{id}")
    public ResponseEntity<HttpStatus> deleteKeyword(@PathVariable("id") long id, Authentication authentication) {
        boolean role = serviceService.isAdminOrProfessional(authentication);
        if (role) {
            keywordRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "Delete all keywords of a service",
            description = "Allows authorized users to delete all keywords associated with a service")
    @ApiResponse(responseCode = "204", description = "Keywords deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @DeleteMapping("/services/{serviceId}/keywords/deleteAllKeywords")
    public ResponseEntity<List<Keyword>> deleteAllKeywordsOfService(@PathVariable(value = "serviceId") Long serviceId, Authentication authentication) throws ResourceNotFoundException {
        boolean role = serviceService.isAdminOrProfessional(authentication);
        if (role) {
            if (!serviceRepository.existsById(serviceId)) {
                throw new ResourceNotFoundException(MESSAGE + serviceId);
            }

            keywordRepository.deleteByServiceId(serviceId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
