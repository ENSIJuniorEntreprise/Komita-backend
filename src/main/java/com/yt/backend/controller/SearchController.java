package com.yt.backend.controller;

import com.yt.backend.model.Search.SearchHistory;
import com.yt.backend.model.Service;
import com.yt.backend.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;

    }

    @Operation(summary = "Search for services", description = "Search for services based on the provided query")
    @ApiResponse(responseCode = "200", description = "Search results returned successfully")
    @ApiResponse(responseCode = "204", description = "No search results found")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @GetMapping("/search")
    public ResponseEntity<List<Service>> searchServices(@RequestParam("query") String query) {
        if (query == null || query.isEmpty()) {
            return ResponseEntity.badRequest().build(); // Return 400 Bad Request if query is null or empty
        }
        SearchHistory searchHistory = searchService.saveSearchHistory(query);
        List<Service> searchResults = searchService.searchServices(query);
        searchService.saveSearchResults(searchResults, searchHistory);
        if (searchResults.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if no search results found
        }
        return ResponseEntity.ok(searchResults);
    }
}
