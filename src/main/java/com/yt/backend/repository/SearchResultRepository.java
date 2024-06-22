package com.yt.backend.repository;

import com.yt.backend.model.Search.SearchResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchResultRepository extends JpaRepository<SearchResult, Long> {
}
