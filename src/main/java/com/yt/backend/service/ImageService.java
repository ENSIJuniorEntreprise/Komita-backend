package com.yt.backend.service;

import com.yt.backend.model.Image;
import com.yt.backend.model.Service;
import com.yt.backend.repository.ImageRepository;
import com.yt.backend.repository.ServiceRepository;
import com.yt.backend.exception.ResourceNotFoundException;
import com.yt.backend.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@org.springframework.stereotype.Service
public class ImageService {
    private final ImageRepository imageRepository;
    private final ServiceRepository serviceRepository;

    public ImageService(ImageRepository imageRepository, ServiceRepository serviceRepository) {
        this.imageRepository = imageRepository;
        this.serviceRepository = serviceRepository;
    }

    public Image addImageToService(MultipartFile file, Long serviceId) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Image file cannot be empty");
        }

        if (!file.getContentType().startsWith("image/")) {
            throw new BusinessException("File must be an image");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException("File size exceeds maximum limit of 5MB");
        }

        com.yt.backend.model.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));

        try {
            Image image = Image.builder()
                .imageData(file.getBytes())
                .contentType(file.getContentType())
                .fileName(file.getOriginalFilename())
                .service(service)
                .build();
            return imageRepository.save(image);
        } catch (IOException e) {
            throw new BusinessException("Failed to process image file: " + e.getMessage());
        }
    }

    public void deleteImage(Long imageId) {
        if (!imageRepository.existsById(imageId)) {
            throw new ResourceNotFoundException("Image not found with id: " + imageId);
        }
        try {
            imageRepository.deleteById(imageId);
        } catch (Exception e) {
            throw new BusinessException("Failed to delete image: " + e.getMessage());
        }
    }

    public List<Image> getAllImages() {
        List<Image> images = imageRepository.findAll();
        if (images.isEmpty()) {
            throw new ResourceNotFoundException("No images found");
        }
        return images;
    }

    public Image getImageById(Long imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));
    }

    public Image updateImage(Long id, MultipartFile file, com.yt.backend.model.Service service) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Image file cannot be empty");
        }

        if (!file.getContentType().startsWith("image/")) {
            throw new BusinessException("File must be an image");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException("File size exceeds maximum limit of 5MB");
        }

        Image existingImage = imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + id));

        try {
            existingImage.setImageData(file.getBytes());
            existingImage.setContentType(file.getContentType());
            existingImage.setFileName(file.getOriginalFilename());
            
            if (service != null) {
                com.yt.backend.model.Service associatedService = serviceRepository.findById(service.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + service.getId()));
                existingImage.setService(associatedService);
            }

            return imageRepository.save(existingImage);
        } catch (IOException e) {
            throw new BusinessException("Failed to process image file: " + e.getMessage());
        } catch (Exception e) {
            throw new BusinessException("Failed to update image: " + e.getMessage());
        }
    }

    public List<Image> getImagesByServiceId(Long serviceId) {
        com.yt.backend.model.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));
        List<Image> images = imageRepository.findByService(service);
        if (images.isEmpty()) {
            throw new ResourceNotFoundException("No images found for service with id: " + serviceId);
        }
        return images;
    }

    public List<Image> getAllImagesByServiceId(Long serviceId) {
        if (!serviceRepository.existsById(serviceId)) {
            throw new ResourceNotFoundException("Service not found with id: " + serviceId);
        }
        
        List<Image> images = imageRepository.findByServiceId(serviceId);
        if (images.isEmpty()) {
            throw new ResourceNotFoundException("No images found for service with id: " + serviceId);
        }
        return images;
    }

    public byte[] getImageData(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));
        
        if (image.getImageData() == null) {
            throw new BusinessException("No image data found for image with id: " + imageId);
        }
        
        return image.getImageData();
    }
}