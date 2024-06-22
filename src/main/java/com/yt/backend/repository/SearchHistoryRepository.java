package com.yt.backend.repository;

import com.yt.backend.model.Search.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository  extends JpaRepository<SearchHistory,Long> {
    List<SearchHistory> findByUserId(Long userId);
}
