package com.yt.backend.service;

import com.yt.backend.dto.CategoryDto;
import com.yt.backend.model.category.Category;
import com.yt.backend.repository.CategoryRepository;
import com.yt.backend.exception.ResourceNotFoundException;
import com.yt.backend.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category addCategory(Category category) {
        if (category == null) {
            throw new BusinessException("Category cannot be null");
        }
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new BusinessException("Category name cannot be empty");
        }
        if (categoryRepository.existsByName(category.getName())) {
            throw new BusinessException("Category with name " + category.getName() + " already exists");
        }
        try {
            return categoryRepository.save(category);
        } catch (Exception e) {
            throw new BusinessException("Failed to create category: " + e.getMessage());
        }
    }

    public void addCategory(CategoryDto categoryDto) {
        if (categoryDto == null) {
            throw new BusinessException("Category DTO cannot be null");
        }
        Category category = CategoryDto.toEntity(categoryDto);
        if (categoryRepository.existsByName(category.getName())) {
            throw new BusinessException("Category with name " + category.getName() + " already exists");
        }
        try {
            categoryRepository.save(category);
        } catch (Exception e) {
            throw new BusinessException("Failed to create category from DTO: " + e.getMessage());
        }
    }

    public List<Category> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new ResourceNotFoundException("No categories found");
        }
        return categories;
    }

    public Category getCategoryById(Long id) {
        if (id == null) {
            throw new BusinessException("Category ID cannot be null");
        }
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    public Category updateCategory(long id, Category category) {
        if (category == null) {
            throw new BusinessException("Category cannot be null");
        }
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new BusinessException("Category name cannot be empty");
        }

        Category existingCategory = getCategoryById(id);
        
        // Check if new name conflicts with another category
        if (!existingCategory.getName().equals(category.getName()) && 
            categoryRepository.existsByName(category.getName())) {
            throw new BusinessException("Category with name " + category.getName() + " already exists");
        }

        try {
            existingCategory.setName(category.getName());
            return categoryRepository.save(existingCategory);
        } catch (Exception e) {
            throw new BusinessException("Failed to update category: " + e.getMessage());
        }
    }

    public void deleteCategory(Long id) {
        if (id == null) {
            throw new BusinessException("Category ID cannot be null");
        }
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        try {
            categoryRepository.deleteById(id);
        } catch (Exception e) {
            throw new BusinessException("Failed to delete category: " + e.getMessage());
        }
    }

    public void deleteAllCategories() {
        try {
            categoryRepository.deleteAll();
        } catch (Exception e) {
            throw new BusinessException("Failed to delete all categories: " + e.getMessage());
        }
    }
}
