package com.yt.backend.controller;

import com.yt.backend.model.Image;
import com.yt.backend.repository.ImageRepository;
import com.yt.backend.repository.ServiceRepository;
import com.yt.backend.service.ImageService;
import com.yt.backend.service.ServiceService;
import com.yt.backend.exception.ResourceNotFoundException;
import com.yt.backend.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class ImageController {

    private final ImageService imageService;
    private final ServiceService serviceService;

    public ImageController(ImageService imageService, ServiceService serviceService) {
        this.imageService = imageService;
        this.serviceService = serviceService;
    }

    @Operation(summary = "Add an image to a service")
    @ApiResponse(responseCode = "201", description = "Image added successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping(value = "/services/{serviceId}/addImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Image> addImage(
            @PathVariable(value = "serviceId") Long serviceId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {
        if (!serviceService.isAdminOrProfessional(authentication)) {
            throw new BusinessException("Insufficient privileges");
        }

        validateImageFile(file);
        Image savedImage = imageService.addImageToService(file, serviceId);
        return new ResponseEntity<>(savedImage, HttpStatus.CREATED);
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("Invalid file type. Only images are allowed");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException("File size too large. Maximum size allowed is 5MB");
        }
    }

    @Operation(summary = "Get all images for a service")
    @ApiResponse(responseCode = "200", description = "Images retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Service not found")
    @GetMapping("/services/{serviceId}/allImages")
    public ResponseEntity<List<Image>> getAllImagesByServiceId(@PathVariable(value = "serviceId") Long serviceId) {
        List<Image> images = imageService.getAllImagesByServiceId(serviceId);
        if (images.isEmpty()) {
            throw new ResourceNotFoundException("No images found for service with id: " + serviceId);
        }
        return ResponseEntity.ok(images);
    }

    @Operation(summary = "Get image data by ID")
    @ApiResponse(responseCode = "200", description = "Image data retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Image not found")
    @GetMapping("/images/{imageId}/data")
    public ResponseEntity<byte[]> getImageData(@PathVariable Long imageId) {
        byte[] imageData = imageService.getImageData(imageId);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageData);
    }

    @Operation(summary = "Delete an image")
    @ApiResponse(responseCode = "204", description = "Image deleted successfully")
    @ApiResponse(responseCode = "404", description = "Image not found")
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long imageId,
            Authentication authentication) {
        if (!serviceService.isAdminOrProfessional(authentication)) {
            throw new BusinessException("Insufficient privileges");
        }

        imageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }

    // DTO class for image metadata
    private static class ImageDTO {
        private final Long id;
        private final String fileName;
        private final String contentType;
        private final String url;

        public ImageDTO(Long id, String fileName, String contentType, String url) {
            this.id = id;
            this.fileName = fileName;
            this.contentType = contentType;
            this.url = url;
        }

        public Long getId() { return id; }
        public String getFileName() { return fileName; }
        public String getContentType() { return contentType; }
        public String getUrl() { return url; }
    }
}