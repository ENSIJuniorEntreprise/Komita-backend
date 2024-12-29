package com.yt.backend.controller;

import com.yt.backend.model.Image;
import com.yt.backend.repository.ImageRepository;
import com.yt.backend.repository.ServiceRepository;
import com.yt.backend.service.ImageService;
import com.yt.backend.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.codehaus.plexus.resource.loader.ResourceNotFoundException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@RequestMapping("/api/v1/auth")
@RestController
public class ImageController {

    private final ImageService imageService;
    private final ServiceService serviceService;
    private final ServiceRepository serviceRepository;
    private final ImageRepository imageRepository;

    public ImageController(ImageService imageService, ServiceService serviceService, ServiceRepository serviceRepository, ImageRepository imageRepository) {
        this.imageService = imageService;
        this.serviceService = serviceService;
        this.serviceRepository = serviceRepository;
        this.imageRepository = imageRepository;
    }

    @Operation(summary = "Add an image to a service",
            description = "Allows authorized users to add a new image to a service")
    @ApiResponse(responseCode = "201", description = "Image added successfully",
            content = @Content(schema = @Schema(implementation = Image.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping("/services/{serviceId}/addImage")
    public ResponseEntity<Image> addImage(@PathVariable(value = "serviceId") Long serviceId, @RequestBody Image requestedImage, Authentication authentication) throws ResourceNotFoundException {
        boolean role = serviceService.isAdminOrProfessional(authentication);
        if (role) {
            Image image = serviceRepository.findById(serviceId).map(service -> {
                requestedImage.setService(service);
                return imageService.addImage(requestedImage);
            }).orElseThrow(() -> new ResourceNotFoundException("Not found service with id" + serviceId));
            return new ResponseEntity<>(image, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "Get all images of a service",
            description = "Retrieves all images associated with a service")
    @ApiResponse(responseCode = "200", description = "List of images retrieved successfully",
            content = @Content(schema = @Schema(implementation = Image.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/services/{serviceId}/allImages")
    public ResponseEntity<List<Image>> getAllImagesByServiceId(@PathVariable(value = "serviceId") Long serviceId, Authentication authentication) throws ResourceNotFoundException{
        boolean role = serviceService.isAdminOrProfessional(authentication);
        if(role){
            if (!serviceRepository.existsById(serviceId)) {
                throw new ResourceNotFoundException("Not found Service with id = " + serviceId);
            }
            List<Image> images = imageRepository.findImageByServiceId(serviceId);
            return new ResponseEntity<>(images,HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        }
    }

    @Operation(summary = "Get an image by ID",
            description = "Retrieves an image by its ID")
    @ApiResponse(responseCode = "200", description = "Image retrieved successfully",
            content = @Content(schema = @Schema(implementation = Image.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/images/{imageId}")
    public ResponseEntity<Image> getImageById(@PathVariable Long imageId, Authentication authentication)  throws ResourceNotFoundException{
        boolean role = serviceService.isAdminOrProfessional(authentication);
        if (role) {
            Image image = imageRepository.findById(imageId)
                    .orElseThrow(() -> new ResourceNotFoundException("Not found image with id = " + imageId));

            return new ResponseEntity<>(image, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "Delete an image by ID",
            description = "Allows authorized users to delete an image by its ID")
    @ApiResponse(responseCode = "204", description = "Image deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @DeleteMapping("/images/delete/{imageId}")
    public ResponseEntity<HttpStatus> deleteImageURL(@PathVariable Long imageId, Authentication authentication) {
        boolean role = serviceService.isAdminOrProfessional(authentication);
        if (role) {
            imageRepository.deleteById(imageId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "Update an image by ID",
            description = "Allows authorized users to update an image by its ID")
    @ApiResponse(responseCode = "200", description = "Image updated successfully",
            content = @Content(schema = @Schema(implementation = Image.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PutMapping("/images/updateImage/{id}")
    public ResponseEntity<Image> updateImage(@PathVariable Long id, @RequestBody Image updatedImage, Authentication authentication) throws ResourceNotFoundException {
        boolean role = serviceService.isAdminOrProfessional(authentication);
        if (role) {
            Image image = imageRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("KeywordId " + id + "not found"));

            image.setImageURL(updatedImage.getImageURL());

            return new ResponseEntity<>(imageRepository.save(image), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

}