package com.yt.backend.controller;

import com.yt.backend.model.Adress;
import com.yt.backend.service.AdressService;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth/adresses")
public class AdressController {

    private final AdressService adressService;
    public AdressController(AdressService adressService) {
        this.adressService = adressService;
    }
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
    public ResponseEntity<Adress> updateAdress(@PathVariable Long id, @RequestBody Adress adress) {
        Adress updatedAdress = adressService.updateAdress(id, adress);
        return ResponseEntity.ok(updatedAdress);
    }

    @Operation(summary = "Delete an address", description = "Delete an existing address by its ID")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAdress(@PathVariable Long id) {
        adressService.deleteAdress(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Create an address", description = "Create a new address")
    @PostMapping("/create")
    public ResponseEntity<Adress> createAdress(@RequestBody Adress address) {
        Adress createdAdress = adressService.createAdress(address);
        return new ResponseEntity<>(createdAdress, HttpStatus.CREATED);
    }
}
