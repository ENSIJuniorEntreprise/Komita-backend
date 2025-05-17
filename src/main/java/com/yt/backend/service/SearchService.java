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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Collections;

@org.springframework.stereotype.Service
public class SearchService {
    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);
    
    private final SearchHistoryRepository searchHistoryRepository;
    private final ServiceRepository serviceRepository;
    private final SearchResultRepository searchResultRepository;

    public SearchService(SearchHistoryRepository searchHistoryRepository, ServiceRepository serviceRepository, SearchResultRepository searchResultRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
        this.serviceRepository = serviceRepository;
        this.searchResultRepository = searchResultRepository;
    }

    @Transactional
    public SearchHistory saveSearchHistory(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            logger.warn("Empty search query received");
            return null;
        }

        try {
            SearchHistory searchHistory = new SearchHistory();
            User currentUser = getCurrentAuthenticatedUser();
            
            if (currentUser == null) {
                logger.warn("No authenticated user found, cannot save search history");
                return null;
            }

            searchHistory.setSearchQuery(searchQuery);
            searchHistory.setUser(currentUser);
            searchHistory.setTimestamp(LocalDateTime.now());
            return searchHistoryRepository.save(searchHistory);
        } catch (Exception e) {
            logger.error("Failed to save search history: {}", e.getMessage(), e);
            return null;
        }
    }

    @Transactional
    public List<com.yt.backend.model.Service> searchServicesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            logger.warn("Empty service name received");
            return Collections.emptyList();
        }

        try {
            logger.info("Searching for services with name containing: {}", name);
            List<com.yt.backend.model.Service> results = serviceRepository.findByNameContainingIgnoreCase(name);
            
            if (results.isEmpty()) {
                logger.info("No services found with name containing: {}", name);
                return Collections.emptyList();
            }
            
            logger.info("Found {} services with name containing: {}", results.size(), name);
            return results;
        } catch (Exception e) {
            logger.error("Failed to search services by name: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private User getCurrentAuthenticatedUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                logger.info("Authentication found: Principal type is {}", 
                           authentication.getPrincipal() != null ? 
                           authentication.getPrincipal().getClass().getName() : "null");
                
                if (authentication.getPrincipal() instanceof User) {
                    User user = (User) authentication.getPrincipal();
                    logger.info("User authenticated: ID={}", user.getId());
                    return user;
                } else {
                    // Si l'utilisateur n'est pas du bon type, créer un utilisateur temporaire pour les tests
                    // Ceci est une solution temporaire pour le débogage
                    User tempUser = new User();
                    tempUser.setId(1L); // Utilisez un ID valide de votre base de données
                    logger.info("Created temporary user with ID={} for testing", tempUser.getId());
                    return tempUser;
                }
            }
        } catch (Exception e) {
            logger.error("Error getting authenticated user: {}", e.getMessage(), e);
        }
        logger.warn("No authentication found in SecurityContext");
        return null;
    }

    public List<com.yt.backend.model.Service> searchServices(String query) {
        if (query == null || query.trim().isEmpty()) {
            logger.warn("Empty search query received");
            return Collections.emptyList();
        }

        try {
            List<String> stemmedQuery = stemWords(query);
            List<com.yt.backend.model.Service> results = serviceRepository.findByMatchingStemmedKeywords(stemmedQuery);
            
            if (results.isEmpty()) {
                logger.info("No services found matching the search criteria: {}", query);
                return Collections.emptyList();
            }
            
            return results;
        } catch (Exception e) {
            logger.error("Failed to perform search: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private List<String> stemWords(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            // Implement your stemming logic here
            // This is a placeholder that just splits the query into words
            return List.of(query.toLowerCase().split("\\s+"));
        } catch (Exception e) {
            logger.error("Error stemming words: {}", e.getMessage(), e);
            return Collections.singletonList(query.toLowerCase());
        }
    }

    @Transactional
    public void saveSearchResults(List<com.yt.backend.model.Service> services, SearchHistory searchHistory) {
        if (searchHistory == null || services == null || services.isEmpty()) {
            logger.warn("Cannot save search results: search history is null or services list is empty");
            return;
        }
        
        try {
            // Implementation of saving search results
            SearchResult searchResult = new SearchResult();
            searchResult.setServices(services);
            searchResult.setSearchHistory(searchHistory);
            SearchResult savedResult = searchResultRepository.save(searchResult);
            
            // Log the saved result ID to verify it was actually saved
            logger.info("Successfully saved search results with ID: {} for query: {}", 
                        savedResult.getId(), searchHistory.getSearchQuery());
        } catch (Exception e) {
            logger.error("Failed to save search results: {}", e.getMessage(), e);
            // Rethrow to trigger transaction rollback
            throw new RuntimeException("Failed to save search results", e);
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
