package com.yt.backend.repository;

import com.yt.backend.model.Keyword;
import com.yt.backend.model.Service;
import com.yt.backend.model.Consultation;
import com.yt.backend.model.category.Category;
import com.yt.backend.model.category.Subcategory;
import com.yt.backend.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
@SuppressWarnings("JpaQlInspection")
public interface ServiceRepository extends JpaRepository<Service, Long> {
    @Override
    Optional<Service> findById(Long aLong);
    Service findServiceById(Long serviceId);

    // Custom query to find services by matching keywords
    @Query("SELECT DISTINCT s FROM Service s JOIN Keyword k ON s.id = k.service.id WHERE LOWER(k.keywordName) IN :keywords")
    List<Service> findByMatchingKeywords(@Param("keywords") List<String> keywords);

    // Custom query to find services by matching stemmed keywords
    @Query("SELECT DISTINCT s FROM Service s JOIN Keyword k ON s.id = k.service.id WHERE LOWER(k.keywordName) IN :keywords")
    List<Service> findByMatchingStemmedKeywords(@Param("keywords") List<String> stemmedKeywords);

    List<Service> findAllByOrderByCreatedAtDesc();

    @Query("SELECT s FROM Service s LEFT JOIN Consultation c ON s.id = c.serviceConsulting.id GROUP BY s ORDER BY COUNT(c) DESC")
    List<Service> findAllByOrderByNbrConsultationsDesc();

    List<Service> findByCategoryAndSubcategory(Category category, Subcategory subcategory);

    List<Service> findByCategory(Category category);

    List<Service> findByProfessional(User user);

    List<Service> findByKeywordListIn(List<Keyword> keywords);

    // Ajoutez cette méthode à votre interface ServiceRepository
    List<Service> findByNameContainingIgnoreCase(String name);
}

