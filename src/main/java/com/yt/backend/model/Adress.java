package com.yt.backend.model;

import jakarta.persistence.Embeddable;
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
@Table(name = "Adress")
public class Adress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long streetNumber;
    private String streetName;
    private String streetType;
    private String provinceName;
    private String postalCode;
    private String city;
    private String country;}


