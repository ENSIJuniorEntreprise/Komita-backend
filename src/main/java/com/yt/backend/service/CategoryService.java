package com.yt.backend.service;

import com.yt.backend.dto.CategoryDto;
import com.yt.backend.model.category.Category;
import com.yt.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.yt.backend.dto.CategoryDto.toEntity;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    public void addCategory(CategoryDto categoryDto){
        categoryRepository.save(CategoryDto.toEntity(categoryDto));
    }
    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }
    public Category getCategoryById(Long id){
        return categoryRepository.findById(id).get() ;
    }
    public void deleteCategory(Long id){
        categoryRepository.deleteById(id);
    }

}
