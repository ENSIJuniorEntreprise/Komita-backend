package com.yt.backend.service;

import com.yt.backend.repository.ServiceRepository;
import com.yt.backend.repository.UserRepository;
import com.yt.backend.exception.ResourceNotFoundException;
import com.yt.backend.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yt.backend.repository.ConsultationRepository;
import com.yt.backend.model.Consultation;
import com.yt.backend.model.user.User;

import java.util.Date;
import java.util.List;

@Service
public class ConsultationService {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceService serviceService;
    
    @Autowired
    private ServiceRepository serviceRepository;

    public ConsultationService(ConsultationRepository consultationRepository) {
        this.consultationRepository = consultationRepository;
    }

    // Method to get or create an anonymous user
    private User getOrCreateAnonymousUser() {
        // Try to find the anonymous user by a specific username or email
        User anonymousUser = userRepository.findByEmail("anonymous@system.com")
;
        
        // If anonymous user doesn't exist, create it
        if (anonymousUser == null) {
            anonymousUser = new User();
            anonymousUser.setFirstname("Anonymous");
            anonymousUser.setLastname("User");
            anonymousUser.setEmail("anonymous@system.com");
            anonymousUser.setPassword("anonymousUserPassword"); // Consider using a secure password
            anonymousUser.setRole(com.yt.backend.model.user.Role.STANDARD_USER);
            
            // Save the anonymous user
            anonymousUser = userService.saveUser(anonymousUser);
        }
        
        return anonymousUser;
    }

    public Consultation createConsultation(Long serviceId, User user) {
        System.out.println("Creating consultation for service ID: " + serviceId);
        if (user != null) {
            System.out.println("User info - ID: " + user.getId() + ", Email: " + user.getEmail() + 
                               ", First name: " + user.getFirstname() + ", Last name: " + user.getLastname());
        } else {
            System.out.println("User is null");
        }
        
        com.yt.backend.model.Service service = serviceRepository.findServiceById(serviceId);
        if (service == null) {
            throw new ResourceNotFoundException("Service not found with id: " + serviceId);
        }
        
        // Use anonymous user if user is null or has incomplete information
        User userToUse;
        if (user == null) {
            userToUse = getOrCreateAnonymousUser();
        } else {
            // Check if this is an authenticated user with an ID and email
            if (user.getId() != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
                // For authenticated users, get the complete user from the database
                userToUse = userRepository.findById(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + user.getId()));
            } else if (isIncompleteUser(user)) {
                // Only use anonymous user for incomplete guest users
                userToUse = getOrCreateAnonymousUser();
            } else {
                // For complete guest users, save them as new users
                userToUse = userService.saveUser(user);
            }
        }
        
        Consultation consultation = new Consultation();
        consultation.setServiceConsulting(service);
        consultation.setUserConsulting(userToUse);
        consultation.setConsultingDate(new Date());
        consultation.setChecked(true);
        
        try {
            Consultation savedConsultation = consultationRepository.save(consultation);
            onConsultationChecked(savedConsultation);
            return savedConsultation;
        } catch (Exception e) {
            throw new BusinessException("Failed to create consultation: " + e.getMessage());
        }
    }
    
    // Helper method to check if a user has incomplete information
    private boolean isIncompleteUser(User user) {
        return user.getEmail() == null || user.getEmail().isEmpty() ||
               user.getFirstname() == null || user.getFirstname().isEmpty() ||
               user.getLastname() == null || user.getLastname().isEmpty();
    }

    // Alternative method that takes userId instead of User object
    public Consultation createConsultation(Long serviceId, Long userId) {
        com.yt.backend.model.Service service = serviceRepository.findServiceById(serviceId);
        if (service == null) {
            throw new ResourceNotFoundException("Service not found with id: " + serviceId);
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Consultation consultation = new Consultation();
        consultation.setServiceConsulting(service);
        consultation.setUserConsulting(user);
        consultation.setConsultingDate(new Date());
        consultation.setChecked(true);
        
        try {
            Consultation savedConsultation = consultationRepository.save(consultation);
            onConsultationChecked(savedConsultation);
            return savedConsultation;
        } catch (Exception e) {
            throw new BusinessException("Failed to create consultation: " + e.getMessage());
        }
    }

    // This method will persist the history of the consultation when the user checks a specific service
    public void onConsultationChecked(Consultation consultation) {
        if (consultation == null || consultation.getServiceConsulting() == null) {
            throw new BusinessException("Invalid consultation data");
        }
        
        com.yt.backend.model.Service service = consultation.getServiceConsulting();
        service.setChecked(true);
    }

    public List<Consultation> getAllConsultations() {
        return consultationRepository.findAll();
    }

    public Consultation getConsultationById(Long id) {
        return consultationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Consultation not found with id: " + id));
    }

    public void saveConsultation(Consultation consultation) {
        if (consultation == null) {
            throw new BusinessException("Consultation data cannot be null");
        }
        try {
            consultationRepository.save(consultation);
        } catch (Exception e) {
            throw new BusinessException("Failed to save consultation: " + e.getMessage());
        }
    }

    public void deleteConsultation(Long id) {
        if (!consultationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Consultation not found with id: " + id);
        }
        try {
            consultationRepository.deleteById(id);
        } catch (Exception e) {
            throw new BusinessException("Failed to delete consultation: " + e.getMessage());
        }
    }

    public List<Consultation> getConsultationsByUser(Long userId) {
        if (!consultationRepository.existsByUserConsultingId(userId)) {
            throw new ResourceNotFoundException("No consultations found for user with id: " + userId);
        }
        return consultationRepository.findByUserConsultingId(userId);
    }

    public List<Consultation> getConsultationsByService(Long serviceId) {
        if (!consultationRepository.existsByServiceConsultingId(serviceId)) {
            throw new ResourceNotFoundException("No consultations found for service with id: " + serviceId);
        }
        return consultationRepository.findByServiceConsultingId(serviceId);
    }
}