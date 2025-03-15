package com.yt.backend.controller;

import com.yt.backend.model.category.Subcategory;
import com.yt.backend.repository.CategoryRepository;
import com.yt.backend.repository.SubcategoryRepository;
import com.yt.backend.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.codehaus.plexus.resource.loader.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SubcategoryController {

    private final SubcategoryRepository subcategoryRepository;
    private final CategoryRepository categoryRepository;
    private final ServiceService serviceService;

    @Operation(summary = "Create a subcategory",
            description = "Allows authorized users to create a new subcategory within a specified category")
    @ApiResponse(responseCode = "201", description = "Subcategory created successfully",
            content = @Content(schema = @Schema(implementation = Subcategory.class)))
    @ApiResponse(responseCode = "400", description = "Bad request or subcategory already exists")
    @PostMapping("/Categories/{categoryId}/SubCategories/CreateSubcategory")
    public ResponseEntity<Optional<Subcategory>> createSubcategory(@PathVariable(value = "categoryId") Long categoryId, @RequestBody Subcategory subcategory, Authentication authentication) {
        boolean isAdmin = serviceService.isAdmin(authentication);
        if (isAdmin) {
            // Check if the subcategory already exists
            if (subcategoryRepository.existsByNameAndCategoryId(subcategory.getName(), categoryId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subcategory with name " + subcategory.getName() + " already exists in the specified category");
            }

            // If the subcategory doesn't exist, proceed to create and save it
            Optional<Subcategory> createdSubcategory = categoryRepository.findById(categoryId).map(category -> {
                subcategory.setCategory(category);
                return subcategoryRepository.save(subcategory);
            });
            return new ResponseEntity<>(createdSubcategory, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }


    @Operation(summary = "Get all subcategories by category ID",
            description = "Retrieve all subcategories belonging to a specified category by its ID")
    @ApiResponse(responseCode = "200", description = "Subcategories retrieved successfully",
            content = @Content(schema = @Schema(implementation = Subcategory.class)))
    @ApiResponse(responseCode = "404", description = "Category not found")
    @GetMapping("/categories/{categoryId}/subCategories")
    public ResponseEntity<List<Subcategory>> getAllSubCategoriesByCategoryId(@PathVariable(value = "categoryId") Long categoryId) throws ResourceNotFoundException {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Not found Category with id = " + categoryId);
        }

        List<Subcategory> subcategories = subcategoryRepository.findByCategoryId(categoryId);
        return new ResponseEntity<>(subcategories, HttpStatus.OK);
    }

    @Operation(summary = "Get subcategory by ID",
            description = "Retrieve a subcategory by its ID")
    @ApiResponse(responseCode = "200", description = "Subcategory retrieved successfully",
            content = @Content(schema = @Schema(implementation = Subcategory.class)))
    @ApiResponse(responseCode = "404", description = "Subcategory not found")
    @GetMapping("/subCategories/{id}")
    public ResponseEntity<Subcategory> getSubcategoryByCategoryId(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        Subcategory subcategory = subcategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Subcategory with id = " + id));

        return new ResponseEntity<>(subcategory, HttpStatus.OK);
    }


    @Operation(summary = "Update a subcategory",
            description = "Allows authorized users to update an existing subcategory")
    @ApiResponse(responseCode = "200", description = "Subcategory updated successfully",
            content = @Content(schema = @Schema(implementation = Subcategory.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PutMapping("/subcategories/updateSubcategory/{id}")
    public ResponseEntity<Subcategory> updateComment(@PathVariable("id") long id, @RequestBody Subcategory subcategory, Authentication authentication) throws ResourceNotFoundException {
        boolean isAdmin = serviceService.isAdmin(authentication);
        if (isAdmin) {
            Subcategory subcategoryy = subcategoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("subcategoryId " + id + "not found"));

            subcategoryy.setName(subcategory.getName());

            return new ResponseEntity<>(subcategoryRepository.save(subcategoryy), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(subcategory, HttpStatus.UNAUTHORIZED);
        }
    }


    @Operation(summary = "Delete a subcategory by ID",
            description = "Allows authorized users to delete a subcategory by its ID")
    @ApiResponse(responseCode = "204", description = "Subcategory deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @DeleteMapping("/subcategories/deleteSubcategory/{id}")
    public ResponseEntity<HttpStatus> deleteSubcategory(@PathVariable("id") long id, Authentication authentication) {
        boolean isAdmin = serviceService.isAdmin(authentication);
        if (isAdmin) {
            subcategoryRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }


    @Operation(summary = "Delete all subcategories of a category",
            description = "Allows authorized users to delete all subcategories belonging to a category by its ID")
    @ApiResponse(responseCode = "204", description = "Subcategories deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @DeleteMapping("/categories/{categoryId}/subCategories/deleteAllSubcategories")
    public ResponseEntity<List<Subcategory>> deleteAllSubCategoriesOfCategory(@PathVariable(value = "categoryId") Long categoryId, Authentication authentication) throws ResourceNotFoundException {
        boolean isAdmin = serviceService.isAdmin(authentication);
        if (isAdmin) {
            if (!categoryRepository.existsById(categoryId)) {
                throw new ResourceNotFoundException("Not found Category with id = " + categoryId);
            }

            subcategoryRepository.deleteByCategoryId(categoryId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
