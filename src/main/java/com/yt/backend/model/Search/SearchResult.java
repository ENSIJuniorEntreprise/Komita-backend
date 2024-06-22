package com.yt.backend.model.Search;

import com.yt.backend.model.Service;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SearchResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "search_result_service",
            joinColumns = @JoinColumn(name = "search_result_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> services;
    @Setter
    @ManyToOne
    @JoinColumn(name = "search_history_id")
    private SearchHistory searchHistory;
}
