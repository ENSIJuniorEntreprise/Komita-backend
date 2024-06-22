package com.yt.backend.repository;

import com.yt.backend.model.category.Subcategory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubcategoryRepository extends JpaRepository<Subcategory,Long> {
    List<Subcategory> findSubcategoryByCategoryId(Long categoryId);

    @Override
    void deleteById(Long aLong);
    @Transactional
    void deleteByCategoryId(Long categoryId);

    List<Subcategory> findByCategoryId(Long aLong);

    boolean existsByNameAndCategoryId(String name, Long categoryId);

}
