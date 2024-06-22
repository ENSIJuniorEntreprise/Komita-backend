package com.yt.backend.service;

import com.yt.backend.model.Adress;
import com.yt.backend.repository.AdressRepositoriy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdressService {
    @Autowired
    private AdressRepositoriy adressRepository;
    public Adress createAdress(Adress address) {

        return adressRepository.save(address);
    }

    public List<Adress> getAllAdresses() {
        return adressRepository.findAll();
    }

    public Adress getAdressById(Long id) {
        return adressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));
    }

    public Adress updateAdress(Long id, Adress newAdressData) {
        Adress existingAdress = adressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));
        existingAdress.setCity(newAdressData.getCity());
        existingAdress.setStreetName(newAdressData.getStreetName());
        existingAdress.setPostalCode(newAdressData.getPostalCode());
        return adressRepository.save(existingAdress);
    }

    public void deleteAdress(Long id) {
        adressRepository.deleteById(id);
    }
}
