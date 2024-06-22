package com.yt.backend.controller;

import com.yt.backend.model.category.Category;
import com.yt.backend.repository.CategoryRepository;
import com.yt.backend.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.codehaus.plexus.resource.loader.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Category Controller", description = "Endpoints for managing categories (admin only)")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final ServiceService serviceService;
    private final static String MESSAGE = "Insufficient privileges";

    @Operation(summary = "Create a new category",
            description = "Allows admin users to create a new category")
    @PostMapping("/addCategory")
    public ResponseEntity<?> CreateCategory(@RequestBody Category category, Authentication authentication){
        boolean role = serviceService.isAdmin(authentication);
        // Check if the role is true
        if (role) {
            // Check if the category already exists
            if (categoryRepository.existsByName(category.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category with name " + category.getName() + " already exists");
            }
            Category _category = categoryRepository.save(new Category(category.getName()));
            return new ResponseEntity<>(_category, HttpStatus.CREATED);

        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(MESSAGE);
        }
    }


    @Operation(summary = "Get all categories",
            description = "Allows users to get all categories"
    )
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories(@RequestParam(required = false) String title) {
        List<Category> categories = new ArrayList<Category>();

        if (title == null)
            categoryRepository.findAll().forEach(categories::add);
        else
            categoryRepository.findByNameContaining(title).forEach(categories::add);

        if (categories.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(categories, HttpStatus.OK);
    }


    @Operation(summary = "Get all categories by their id",
            description = "Allows users to get all categories by their id"
    )
    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable("id") long id) throws ResourceNotFoundException {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Category with id = " + id));

        return new ResponseEntity<>(category, HttpStatus.OK);
    }


    @Operation(summary = "Updates a category by its id",
            description = "Allows admin users to update a category by their id"
    )
    @PutMapping("/categories/updateCategory/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable("id") long id, @RequestBody Category category,Authentication authentication) throws ResourceNotFoundException {
        boolean role = serviceService.isAdmin(authentication);
        if (role){
            Category _category = categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Not found Category with id = " + id));

            _category.setName(category.getName());
            return new ResponseEntity<>(categoryRepository.save(_category), HttpStatus.OK);
        }
        else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(MESSAGE);
        }

    }


    @Operation(summary = "Delete a category by its id",
            description = "Allows admin users to delete a category by its id"
    )
    @DeleteMapping("/categories/deleteCategory/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable("id") long id,Authentication authentication) {
        boolean role = serviceService.isAdmin(authentication);
        if (role){
            categoryRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(MESSAGE);
        }
    }


    @Operation(summary = "Delete all categories",
            description = "Allows admin users to delete all categories"
    )
    @DeleteMapping("/categories")
    public ResponseEntity<?> deleteAllCategories(Authentication authentication) {
        boolean role = serviceService.isAdmin(authentication);
        if (role){
            categoryRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(MESSAGE);
        }
    }
}
