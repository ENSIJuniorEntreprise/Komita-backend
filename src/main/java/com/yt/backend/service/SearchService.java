package com.yt.backend.service;

import com.yt.backend.model.Search.SearchHistory;
import com.yt.backend.model.Search.SearchResult;
import com.yt.backend.model.user.User;
import com.yt.backend.repository.SearchHistoryRepository;
import com.yt.backend.repository.SearchResultRepository;
import com.yt.backend.repository.ServiceRepository;
import com.yt.backend.exception.ResourceNotFoundException;
import com.yt.backend.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Service
public class SearchService {
    private final SearchHistoryRepository searchHistoryRepository;
    private final ServiceRepository serviceRepository;
    private final SearchResultRepository searchResultRepository;

    public SearchService(SearchHistoryRepository searchHistoryRepository, ServiceRepository serviceRepository, SearchResultRepository searchResultRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
        this.serviceRepository = serviceRepository;
        this.searchResultRepository = searchResultRepository;
    }

    public SearchHistory saveSearchHistory(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            throw new BusinessException("Search query cannot be empty");
        }

        try {
            SearchHistory searchHistory = new SearchHistory();
            User currentUser = getCurrentAuthenticatedUser();
            
            if (currentUser == null) {
                throw new BusinessException("No authenticated user found");
            }

            searchHistory.setSearchQuery(searchQuery);
            searchHistory.setUser(currentUser);
            searchHistory.setTimestamp(LocalDateTime.now());
            return searchHistoryRepository.save(searchHistory);
        } catch (Exception e) {
            throw new BusinessException("Failed to save search history: " + e.getMessage());
        }
    }

    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    public List<com.yt.backend.model.Service> searchServices(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new BusinessException("Search query cannot be empty");
        }

        try {
            List<String> stemmedQuery = stemWords(query);
            List<com.yt.backend.model.Service> results = serviceRepository.findByMatchingStemmedKeywords(stemmedQuery);
            
            if (results.isEmpty()) {
                throw new ResourceNotFoundException("No services found matching the search criteria");
            }
            
            return results;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Failed to perform search: " + e.getMessage());
        }
    }

    private List<String> stemWords(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new BusinessException("Cannot stem empty query");
        }
        // Implement your stemming logic here
        // This is a placeholder that just splits the query into words
        return List.of(query.toLowerCase().split("\\s+"));
    }

    public void saveSearchResults(List<com.yt.backend.model.Service> services, SearchHistory searchHistory) {
        if (searchHistory == null) {
            throw new BusinessException("Search history cannot be null");
        }
        
        try {
            // Implementation of saving search results
            // This would typically involve creating a SearchResult entity and saving it
            SearchResult searchResult = new SearchResult();
            searchResult.setServices(services);
            searchResult.setSearchHistory(searchHistory);
            searchResultRepository.save(searchResult);
        } catch (Exception e) {
            throw new BusinessException("Failed to save search results: " + e.getMessage());
        }
    }

    public List<SearchHistory> getSearchHistoryByUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException("User ID cannot be null");
        }
        
        List<SearchHistory> history = searchHistoryRepository.findByUserId(userId);
        if (history.isEmpty()) {
            throw new ResourceNotFoundException("No search history found for user with id: " + userId);
        }
        return history;
    }
}
