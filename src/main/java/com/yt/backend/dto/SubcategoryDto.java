package com.yt.backend.dto;

import com.yt.backend.model.category.Category;
import com.yt.backend.model.category.Subcategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubcategoryDto {
    private Long id;
    private String name;
    private CategoryDto category;
    public static Subcategory toEntity(SubcategoryDto subcategoryDto) {
        Subcategory subcategory = new Subcategory();
        subcategory.setId(subcategoryDto.getId());
        subcategory.setName(subcategoryDto.getName());
        subcategory.setCategory(CategoryDto.toEntity(subcategoryDto.getCategory()));
        return subcategory;
    }

    public static SubcategoryDto fromEntity(Subcategory subcategory) {
        return SubcategoryDto.builder()
                .id(subcategory.getId())
                .name(subcategory.getName())
                .build();
    }
}
