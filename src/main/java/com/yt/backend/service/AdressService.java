package com.yt.backend.service;

import com.yt.backend.model.Adress;
import com.yt.backend.repository.AdressRepositoriy;
import com.yt.backend.exception.ResourceNotFoundException;
import com.yt.backend.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdressService {
    private final AdressRepositoriy adressRepository;

    public Adress createAdress(Adress address) {
        if (address == null) {
            throw new BusinessException("Address cannot be null");
        }
        if (address.getCity() == null || address.getCity().trim().isEmpty()) {
            throw new BusinessException("City cannot be empty");
        }
        if (address.getStreetName() == null || address.getStreetName().trim().isEmpty()) {
            throw new BusinessException("Street name cannot be empty");
        }
        if (address.getPostalCode() == null || address.getPostalCode().trim().isEmpty()) {
            throw new BusinessException("Postal code cannot be empty");
        }

        try {
            return adressRepository.save(address);
        } catch (Exception e) {
            throw new BusinessException("Failed to create address: " + e.getMessage());
        }
    }

    public List<Adress> getAllAdresses() {
        List<Adress> addresses = adressRepository.findAll();
        if (addresses.isEmpty()) {
            throw new ResourceNotFoundException("No addresses found");
        }
        return addresses;
    }

    public Adress getAdressById(Long id) {
        if (id == null) {
            throw new BusinessException("Address ID cannot be null");
        }
        return adressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));
    }

    public Adress updateAdress(Long id, Adress newAdressData) {
        if (id == null) {
            throw new BusinessException("Address ID cannot be null");
        }
        if (newAdressData == null) {
            throw new BusinessException("New address data cannot be null");
        }
        if (newAdressData.getCity() == null || newAdressData.getCity().trim().isEmpty()) {
            throw new BusinessException("City cannot be empty");
        }
        if (newAdressData.getStreetName() == null || newAdressData.getStreetName().trim().isEmpty()) {
            throw new BusinessException("Street name cannot be empty");
        }
        if (newAdressData.getPostalCode() == null || newAdressData.getPostalCode().trim().isEmpty()) {
            throw new BusinessException("Postal code cannot be empty");
        }

        try {
            Adress existingAdress = getAdressById(id);
            existingAdress.setCity(newAdressData.getCity());
            existingAdress.setStreetName(newAdressData.getStreetName());
            existingAdress.setPostalCode(newAdressData.getPostalCode());
            return adressRepository.save(existingAdress);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Failed to update address: " + e.getMessage());
        }
    }

    public void deleteAdress(Long id) {
        if (id == null) {
            throw new BusinessException("Address ID cannot be null");
        }
        if (!adressRepository.existsById(id)) {
            throw new ResourceNotFoundException("Address not found with id: " + id);
        }
        try {
            adressRepository.deleteById(id);
        } catch (Exception e) {
            throw new BusinessException("Failed to delete address: " + e.getMessage());
        }
    }
}
