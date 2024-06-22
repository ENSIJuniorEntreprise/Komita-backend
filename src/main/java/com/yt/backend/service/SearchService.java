package com.yt.backend.service;

import com.yt.backend.model.Search.SearchHistory;
import com.yt.backend.model.Search.SearchResult;
import com.yt.backend.model.user.User;
import com.yt.backend.repository.SearchHistoryRepository;
import com.yt.backend.repository.SearchResultRepository;
import com.yt.backend.repository.ServiceRepository;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
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
        SearchHistory searchHistory = new SearchHistory();
        User current_authenticated_user = getCurrentAuthenticatedUser();
        searchHistory.setSearchQuery(searchQuery);
        searchHistory.setUser(current_authenticated_user);
        searchHistory.setTimestamp(LocalDateTime.now());
        searchHistoryRepository.save(searchHistory);
        return searchHistory;
    }

    private User getCurrentAuthenticatedUser() {
        // Get the authentication object from the SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    public List<com.yt.backend.model.Service> searchServices(String query) {
        List<String> stemmedQuery = stemWords(query);
        // Query the database for services based on matching stemmed keywords
        return serviceRepository.findByMatchingStemmedKeywords(stemmedQuery);
    }

    private List<String> stemWords(String text) {
        List<String> stemmedWords = new ArrayList<>();

        try (StringReader reader = new StringReader(text);
             StandardTokenizer tokenizer = new StandardTokenizer()) {

            tokenizer.setReader(reader);

            // Use PorterStemFilter for stemming
            TokenStream tokenStream = new PorterStemFilter(new LowerCaseFilter(tokenizer));

            // Get the stemmed words
            CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();

            while (tokenStream.incrementToken()) {
                stemmedWords.add(charTermAttribute.toString());
            }

            tokenStream.end();
        } catch (IOException e) {
            // Handle the exception appropriately, e.g., log it or throw a custom exception
            e.printStackTrace();
        }
        return stemmedWords;
    }
    public void saveSearchResults(List<com.yt.backend.model.Service> services, SearchHistory searchHistory) {
        // Assuming SearchResult is an entity representing the search results
        SearchResult searchResult = new SearchResult();
        searchResult.setServices(services);
        searchResult.setSearchHistory(searchHistory);
        searchResultRepository.save(searchResult);
    }
    public List<SearchHistory> getSearchHistoryByUserId(Long userId) {
        // Implement logic to retrieve search history based on user ID
        return searchHistoryRepository.findByUserId(userId);
    }
}
