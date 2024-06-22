package com.yt.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String imageURL;
    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;
    public String getImageURL() {
        return imageURL;
    }
    public void setImageURL(String imageUrl) {
        this.imageURL = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public Service getService() {
        return service;
    }
}
