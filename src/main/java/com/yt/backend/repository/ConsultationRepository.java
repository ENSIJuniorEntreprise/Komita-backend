package com.yt.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.yt.backend.model.Consultation;
import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByUserConsultingId(Long userId);
    List<Consultation> findByServiceConsultingId(Long serviceId);
    boolean existsByUserConsultingId(Long userId);
    boolean existsByServiceConsultingId(Long serviceId);
}
