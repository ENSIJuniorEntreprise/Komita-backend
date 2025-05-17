package com.yt.backend.controller;

import com.yt.backend.model.Adress;
import com.yt.backend.service.AdressService;
import com.yt.backend.exception.ValidationException;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/adresses")
@RequiredArgsConstructor
public class AdressController {

    private final AdressService adressService;

    @Operation(summary = "Get all addresses", description = "Retrieve a list of all addresses")
    @GetMapping("/allAdress")
    public ResponseEntity<List<Adress>> getAllAdresses() {
        List<Adress> allAdresses = adressService.getAllAdresses();
        return ResponseEntity.ok(allAdresses);
    }

    @Operation(summary = "Get address by ID", description = "Retrieve an address by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<Adress> getAdressById(@PathVariable Long id) {
        Adress adress = adressService.getAdressById(id);
        return ResponseEntity.ok(adress);
    }

    @Operation(summary = "Update an address", description = "Update an existing address by its ID")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAdress(@PathVariable Long id, @RequestBody Adress adress) {
        try {
            // Validation personnalisée
            validateAddress(adress);
            
            Adress updatedAdress = adressService.updateAdress(id, adress);
            return ResponseEntity.ok(updatedAdress);
        } catch (ValidationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getErrors());
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Une erreur est survenue lors de la mise à jour de l'adresse: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @Operation(summary = "Delete an address", description = "Delete an existing address by its ID")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAdress(@PathVariable Long id) {
        adressService.deleteAdress(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Create an address", description = "Create a new address")
    @PostMapping("/create")
    public ResponseEntity<?> createAdress(@RequestBody Adress address) {
        try {
            // Validation personnalisée
            validateAddress(address);
            
            Adress createdAdress = adressService.createAdress(address);
            return new ResponseEntity<>(createdAdress, HttpStatus.CREATED);
        } catch (ValidationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getErrors());
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Une erreur est survenue lors de la création de l'adresse: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Méthode utilitaire pour valider une adresse
     */
    private void validateAddress(Adress address) {
        Map<String, String> validationErrors = new HashMap<>();
        
        // Vérification si l'adresse est null
        if (address == null) {
            validationErrors.put("address", "L'adresse ne peut pas être vide");
            throw new ValidationException("Adresse invalide", validationErrors);
        }
    
        
        // Validation de la ville
        String city = address.getCity();
        if (city == null || city.isEmpty()) {
            validationErrors.put("city", "La ville est obligatoire");
        }
        
        // Validation du code postal
        String zipCode = address.getPostalCode();
        if (zipCode == null || zipCode.isEmpty()) {
            validationErrors.put("zipCode", "Le code postal est obligatoire");
        } else if (!zipCode.matches("^[0-9]{4,5}$")) {
            validationErrors.put("zipCode", "Le code postal doit contenir 4 ou 5 chiffres");
        }
        
        // Validation du pays
        String country = address.getCountry();
        if (country == null || country.isEmpty()) {
            validationErrors.put("country", "Le pays est obligatoire");
        }
        
        // Si des erreurs de validation sont présentes, lancer l'exception
        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Erreurs de validation de l'adresse", validationErrors);
        }
    }
}
