package com.yt.backend.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yt.backend.model.category.Category;
import com.yt.backend.model.category.Subcategory;
import com.yt.backend.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="service_table")
public class Service {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String description;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;
    @OneToOne
    private Category category;
    @OneToOne
    private Subcategory subcategory;
    @OneToOne
    private User professional;
    private Boolean state;
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @Getter
    @OneToOne
    private Adress adress;
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Keyword> keywordList = new ArrayList<>();
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @Setter
    @OneToOne
    private Links links;
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();
    @Setter
    private Boolean checked;
    public Service(String name, String description, User professional, Category category, Subcategory subcategory, Boolean state,Adress adress,Links serviceLinks) {
        this.name = name;
        this.description = description;
        this.professional = professional;
        this.state = state;
        this.category = category;
        this.subcategory = subcategory;
        this.adress = adress;
        this.links = serviceLinks;
    }
    public void setAdress(Adress adress) {
        this.adress = adress;
    }
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = this.createdAt;
    }
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }
     // Helper method to add a keyword
     public void addKeyword(Keyword keyword) {
        keyword.setService(this); // Set the bidirectional relationship
        this.keywordList.add(keyword);
    }

    // Helper method to remove a keyword
    public void removeKeyword(Keyword keyword) {
        this.keywordList.remove(keyword);
        keyword.setService(null); // Remove the bidirectional relationship
    }

    // Ensure the collection is never replaced
    public void setKeywordList(List<Keyword> keywordList) {
        this.keywordList.clear(); // Clear the existing collection
        if (keywordList != null) {
            this.keywordList.addAll(keywordList); // Add all new keywords
        }
    }
}
