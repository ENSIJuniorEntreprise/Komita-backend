package com.yt.backend.service;

import com.yt.backend.model.Image;
import com.yt.backend.repository.ImageRepository;
import com.yt.backend.repository.ServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ImageService {
    private final ImageRepository imageRepository;
    private final ServiceRepository serviceRepository;
    public ImageService(ImageRepository imageRepository, ServiceRepository serviceRepository) {
        this.imageRepository = imageRepository;
        this.serviceRepository = serviceRepository;
    }

    public Image addImage(Image image) {
        return imageRepository.save(image);
    }

    public void deleteImageURL(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));

        image.setImageURL(null);
        imageRepository.save(image);
    }

    public List<Image> getAllImages() {

        return imageRepository.findAll();
    }

    public Image getImageById(Long imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));
    }

    public Image updateImage(Long id, Image updatedImage)  {
        Image existingImage = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + id));

        existingImage.setImageURL(updatedImage.getImageURL());

        Long serviceId = updatedImage.getService().getId();
        com.yt.backend.model.Service associatedService = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + serviceId));

        existingImage.setService(associatedService);
        return imageRepository.save(existingImage);
    }
}