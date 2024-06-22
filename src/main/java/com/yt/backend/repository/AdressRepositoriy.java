package com.yt.backend.repository;

import com.yt.backend.model.Adress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdressRepositoriy  extends JpaRepository<Adress, Long> {
}
