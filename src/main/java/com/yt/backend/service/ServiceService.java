package com.yt.backend.service;

import com.yt.backend.exception.ResourceNotFoundException;
import com.yt.backend.model.Service;
import com.yt.backend.model.ServiceState;
import com.yt.backend.repository.ServiceRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;

@org.springframework.stereotype.Service
@AllArgsConstructor
public class ServiceService {
    
    private final ServiceRepository serviceRepository;

    public boolean isAdminOrProfessional(Authentication authentication){
        return authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ADMIN") || role.getAuthority().equals("PROFESSIONAL"));
    }

    public boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ADMIN"));
    }
    
    public Service updateServiceState(long serviceId, ServiceState newState) {
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));
        
        service.setState(newState);
        return serviceRepository.save(service);
    }
}
