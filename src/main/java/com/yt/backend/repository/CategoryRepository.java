package com.yt.backend.repository;

import com.yt.backend.model.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    @Override
    List<Category> findAll();

    @Override
    Optional<Category> findById(Long aLong);

    List<Category> findByNameContaining(String title);

    boolean existsByName(String name);
}
