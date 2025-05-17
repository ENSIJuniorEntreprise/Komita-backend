package com.yt.backend.controller;

import com.yt.backend.model.Search.SearchHistory;
import com.yt.backend.model.Service;
import com.yt.backend.model.ServiceState;
import com.yt.backend.service.SearchService;
import com.yt.backend.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

@RestController
@RequestMapping("/api/v1")
public class SearchController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @Operation(summary = "Search for services", description = "Search for active services based on the provided query")
    @ApiResponse(responseCode = "200", description = "Search results returned successfully")
    @ApiResponse(responseCode = "400", description = "Invalid search query")
    @GetMapping("/search")
    public ResponseEntity<List<Service>> searchServices(@RequestParam("query") String query) {
        try {
            logger.info("Received search request with query: {}", query);
            
            // Vérifier si la requête est valide
            if (query == null || query.trim().isEmpty()) {
                logger.warn("Empty search query received");
                return ResponseEntity.ok(Collections.emptyList());
            }
            
            SearchHistory searchHistory = searchService.saveSearchHistory(query);
            logger.info("Search history saved: {}", searchHistory != null ? "success" : "failed");
            
            // Get all search results
            List<Service> allSearchResults;
            try {
                allSearchResults = searchService.searchServices(query);
                logger.info("Found {} services matching the query", allSearchResults.size());
            } catch (Exception e) {
                logger.error("Error during search: {}", e.getMessage(), e);
                allSearchResults = Collections.emptyList();
            }
            
            // Filter to only include ACTIVE services
            List<Service> activeServices = allSearchResults.stream()
                    .filter(service -> ServiceState.ACTIVE.equals(service.getState()))
                    .collect(Collectors.toList());
            logger.info("Filtered to {} active services", activeServices.size());
            
            // Save the filtered results
            if (!activeServices.isEmpty() && searchHistory != null) {
                logger.info("Saving search results...");
                searchService.saveSearchResults(activeServices, searchHistory);
            } else {
                logger.warn("Not saving search results: activeServices={}, searchHistory={}", 
                          activeServices.isEmpty() ? "empty" : "not empty", 
                          searchHistory == null ? "null" : "not null");
            }
            
            return ResponseEntity.ok(activeServices);
        } catch (Exception e) {
            logger.error("Unhandled exception in searchServices: {}", e.getMessage(), e);
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @Operation(summary = "Search for services by name", description = "Search for active services by name")
    @ApiResponse(responseCode = "200", description = "Search results returned successfully")
    @ApiResponse(responseCode = "400", description = "Invalid service name")
    @GetMapping("/search/name")
    public ResponseEntity<List<Service>> searchServicesByName(@RequestParam("name") String name) {
        try {
            logger.info("Received search by name request: {}", name);
            
            // Vérifier si le nom est valide
            if (name == null || name.trim().isEmpty()) {
                logger.warn("Empty service name received");
                return ResponseEntity.ok(Collections.emptyList());
            }
            
            SearchHistory searchHistory = searchService.saveSearchHistory("name:" + name);
            logger.info("Search history saved: {}", searchHistory != null ? "success" : "failed");
            
            // Get services by name
            List<Service> servicesByName;
            try {
                servicesByName = searchService.searchServicesByName(name);
                logger.info("Found {} services matching the name", servicesByName.size());
            } catch (Exception e) {
                logger.error("Error during search by name: {}", e.getMessage(), e);
                servicesByName = Collections.emptyList();
            }
            
            // Filter to only include ACTIVE services
            List<Service> activeServices = servicesByName.stream()
                    .filter(service -> ServiceState.ACTIVE.equals(service.getState()))
                    .collect(Collectors.toList());
            logger.info("Filtered to {} active services", activeServices.size());
            
            // Save the filtered results
            if (!activeServices.isEmpty() && searchHistory != null) {
                logger.info("Saving search results...");
                searchService.saveSearchResults(activeServices, searchHistory);
            }
            
            return ResponseEntity.ok(activeServices);
        } catch (Exception e) {
            logger.error("Unhandled exception in searchServicesByName: {}", e.getMessage(), e);
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @Operation(summary = "Get search history by user", description = "Retrieve search history for a specific user")
    @ApiResponse(responseCode = "200", description = "Search history retrieved successfully")
    @ApiResponse(responseCode = "404", description = "No search history found")
    @GetMapping("/search/history/{userId}")
    public ResponseEntity<List<SearchHistory>> getSearchHistoryByUser(@PathVariable Long userId) {
        List<SearchHistory> history = searchService.getSearchHistoryByUserId(userId);
        return ResponseEntity.ok(history);
    }
}
