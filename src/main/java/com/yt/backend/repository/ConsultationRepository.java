package com.yt.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.yt.backend.model.Consultation;


@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

}
