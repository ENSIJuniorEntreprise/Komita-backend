package com.yt.backend.service;

import com.yt.backend.repository.ServiceRepository;
import org.hibernate.service.spi.ServiceException;
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

    public Consultation createConsultation(Long serviceId, User user) {
        com.yt.backend.model.Service service = serviceRepository.findServiceById(serviceId);
        if (service == null) {
            throw new ServiceException("Service not found with id: " + serviceId);
        }
        Consultation consultation = new Consultation();
        consultation.setServiceConsulting(service);
        consultation.setUserConsulting(user);
        consultation.setConsultingDate(new Date());
        consultation.setChecked(true);
        // Save the consultation
        consultationRepository.save(consultation);
        // Notify the listener
        onConsultationChecked(consultation);
        return consultation;
    }

    // This method will persist the history of the consultation when the user checks a specific service
    public void onConsultationChecked(Consultation consultation) {
        com.yt.backend.model.Service service = consultation.getServiceConsulting();
        service.setChecked(true);
    }

    public List<Consultation> getAllConsultations() {

        return consultationRepository.findAll();
    }

    public Consultation getConsultationById(Long id) {

        return consultationRepository.findById(id).orElse(null);
    }

    public ConsultationService(ConsultationRepository consultationRepository) {
        this.consultationRepository = consultationRepository;
    }

    public void saveConsultation(Consultation consultation) {
        consultationRepository.save(consultation);
    }

    public void deleteConsultation(Long id) {
        consultationRepository.deleteById(id);
    }

    public Consultation getConsultationsByUser(Long userId) {
        return consultationRepository.findById(userId).orElse(null);
    }

    public Consultation getConsultationsByService(Long serviceId) {
        return consultationRepository.findById(serviceId).orElse(null);}

}
