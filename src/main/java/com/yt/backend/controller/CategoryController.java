package com.yt.backend.controller;

import com.yt.backend.model.category.Category;
import com.yt.backend.service.CategoryService;
import com.yt.backend.service.ServiceService;
import com.yt.backend.exception.ResourceNotFoundException;
import com.yt.backend.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Category Controller", description = "Endpoints for managing categories (admin only)")
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService categoryService;
    private final ServiceService serviceService;

    @Operation(summary = "Create a new category", description = "Allows admin users to create a new category")
    @ApiResponse(responseCode = "201", description = "Category created successfully")
    @ApiResponse(responseCode = "403", description = "Insufficient privileges")
    @PostMapping("/addCategory")
    public ResponseEntity<Category> createCategory(@RequestBody Category category, Authentication authentication) {
        if (!serviceService.isAdmin(authentication)) {
            throw new BusinessException("Insufficient privileges");
        }
        
        Category savedCategory = categoryService.addCategory(category);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all categories", description = "Allows users to get all categories")
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    @ApiResponse(responseCode = "404", description = "No categories found")
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        if (categories.isEmpty()) {
            throw new ResourceNotFoundException("No categories found");
        }
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Get category by ID", description = "Allows users to get a category by its ID")
    @ApiResponse(responseCode = "200", description = "Category retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable("id") long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Update a category", description = "Allows admin users to update an existing category")
    @ApiResponse(responseCode = "200", description = "Category updated successfully")
    @ApiResponse(responseCode = "403", description = "Insufficient privileges")
    @PutMapping("/categories/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable("id") long id,
            @RequestBody Category category,
            Authentication authentication) {
        if (!serviceService.isAdmin(authentication)) {
            throw new BusinessException("Insufficient privileges");
        }
        
        Category updatedCategory = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(updatedCategory);
    }

    @Operation(summary = "Delete a category", description = "Allows admin users to delete a category")
    @ApiResponse(responseCode = "204", description = "Category deleted successfully")
    @ApiResponse(responseCode = "403", description = "Insufficient privileges")
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") long id, Authentication authentication) {
        if (!serviceService.isAdmin(authentication)) {
            throw new BusinessException("Insufficient privileges");
        }
        
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
