package com.yt.backend.repository;

import com.yt.backend.model.Image;
import com.yt.backend.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByService(Service service);
    List<Image> findByServiceId(Long serviceId);
}
