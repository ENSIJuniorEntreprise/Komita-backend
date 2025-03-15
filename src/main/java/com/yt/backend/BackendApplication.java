package com.yt.backend;

import com.yt.backend.dto.RegisterRequest;
import com.yt.backend.service.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static com.yt.backend.model.user.Role.*;


@SpringBootApplication(scanBasePackages = "com.yt.backend")
public class BackendApplication {

    public static void main(String[] args) {

        SpringApplication.run(BackendApplication.class, args);
    }
}
