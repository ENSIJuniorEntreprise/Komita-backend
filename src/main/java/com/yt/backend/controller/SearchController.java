package com.yt.backend.controller;

import com.yt.backend.model.Search.SearchHistory;
import com.yt.backend.model.Service;
import com.yt.backend.service.SearchService;
import com.yt.backend.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @Operation(summary = "Search for services", description = "Search for services based on the provided query")
    @ApiResponse(responseCode = "200", description = "Search results returned successfully")
    @ApiResponse(responseCode = "400", description = "Invalid search query")
    @GetMapping("/search")
    public ResponseEntity<List<Service>> searchServices(@RequestParam("query") String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new BusinessException("Search query cannot be empty");
        }
        
        SearchHistory searchHistory = searchService.saveSearchHistory(query);
        List<Service> searchResults = searchService.searchServices(query);
        searchService.saveSearchResults(searchResults, searchHistory);
        return ResponseEntity.ok(searchResults);
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
