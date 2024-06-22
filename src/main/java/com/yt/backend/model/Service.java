package com.yt.backend.model;
import com.yt.backend.model.category.Category;
import com.yt.backend.model.category.Subcategory;
import com.yt.backend.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;


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
    @OneToMany(mappedBy = "id")
    private List<Keyword> keywordList;
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @Setter
    @OneToOne
    private Links links;
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @OneToMany(mappedBy = "id")
    private List<Image> images;

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
}
