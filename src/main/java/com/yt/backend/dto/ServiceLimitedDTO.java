package com.yt.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceLimitedDTO {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private Long subcategoryId;
    private String subcategoryName;
    private Long professionalId;
    private String professionalName;
    private byte[] professionalProfileImage;
    private List<String> keywords;
    private Long primaryImageId;
    private Date createdAt;
    private String city;
    // Vous pouvez ajouter d'autres champs limités si nécessaire
}