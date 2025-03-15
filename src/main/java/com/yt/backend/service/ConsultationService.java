package com.yt.backend.service;

import com.yt.backend.repository.ServiceRepository;
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
    private ServiceService serviceService;
    @Autowired
    private ServiceRepository serviceRepository;

    public ConsultationService(ConsultationRepository consultationRepository) {
        this.consultationRepository = consultationRepository;
    }

    public Consultation createConsultation(Long serviceId, User user) {
        com.yt.backend.model.Service service = serviceRepository.findServiceById(serviceId);
        if (service == null) {
            throw new ResourceNotFoundException("Service not found with id: " + serviceId);
        }
        
        if (user == null) {
            throw new BusinessException("User information is required");
        }

        Consultation consultation = new Consultation();
        consultation.setServiceConsulting(service);
        consultation.setUserConsulting(user);
        consultation.setConsultingDate(new Date());
        consultation.setChecked(true);
        
        try {
            consultationRepository.save(consultation);
            onConsultationChecked(consultation);
            return consultation;
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
