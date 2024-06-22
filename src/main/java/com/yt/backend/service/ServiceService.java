package com.yt.backend.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
@Service
@AllArgsConstructor
public class ServiceService {

    public boolean isAdminOrProfessional(Authentication authentication){
        return authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ADMIN") || role.getAuthority().equals("PROFESSIONAL"));
    }

    public boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ADMIN"));
    }
}
