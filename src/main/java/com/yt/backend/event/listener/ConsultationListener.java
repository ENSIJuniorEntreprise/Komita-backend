package com.yt.backend.event.listener;

import com.yt.backend.model.user.Role;
import com.yt.backend.model.user.User;
import com.yt.backend.model.Consultation;
import org.springframework.stereotype.Service;

import java.util.List;

import com.yt.backend.model.Consultation;

@Service
public interface ConsultationListener {
    void onConsultationChecked(Consultation consultation);
}
