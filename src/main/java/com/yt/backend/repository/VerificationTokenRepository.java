package com.yt.backend.repository;

import com.yt.backend.security.VerificationToken;
import com.yt.backend.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
    void deleteByUser(User user);
}
