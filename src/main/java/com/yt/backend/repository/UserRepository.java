package com.yt.backend.repository;

import com.yt.backend.model.user.Role;
import com.yt.backend.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);
    User findByPhoneNumber(String phoneNumber);


    List<User> findByRole(Role role);

    boolean existsByEmail(String email);
}
