package com.yt.backend.repository;

import com.yt.backend.model.Keyword;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    List<Keyword> findKeywordByServiceId(Long id);
   @Transactional
    void deleteByServiceId(long serviceId);

    List<Keyword> findByKeywordNameIgnoreCase(String keywordName);

    List<Keyword> findByKeywordNameStartingWithIgnoreCase(String prefix);   


}
