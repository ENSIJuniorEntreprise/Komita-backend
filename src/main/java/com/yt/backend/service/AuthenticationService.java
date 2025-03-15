package com.yt.backend.service;

import com.yt.backend.dto.AuthenticationRequest;
import com.yt.backend.dto.AuthenticationResponse;
import com.yt.backend.dto.RegisterRequest;
import com.yt.backend.dto.RegisterResponse;
import com.yt.backend.event.RegistrationCompleteEvent;
import com.yt.backend.model.user.Role;
import com.yt.backend.model.user.User;
import com.yt.backend.repository.TokenRepository;
import com.yt.backend.repository.UserRepository;
import com.yt.backend.repository.VerificationTokenRepository;
import com.yt.backend.security.JwtService;
import com.yt.backend.security.Token;
import com.yt.backend.security.TokenType;
import com.yt.backend.security.VerificationToken;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher publisher;


    public RegisterResponse register(RegisterRequest request, HttpServletRequest httpRequest) {
        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .userAddress(request.getUserAddress())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .status(false)  // Explicitly set status to false for email verification
                .build();
        String customIdentifier = generateCustomIdentifier(user.getRole());
        user.setCustomIdentifier(customIdentifier);
        User savedUser = repository.save(user);
        String jwtToken = jwtService.generateToken(savedUser);
        saveUserToken(savedUser, jwtToken);
        RegisterResponse registrationResponse = RegisterResponse.builder()
                .firstname(savedUser.getFirstname())
                .lastname(savedUser.getLastname())
                .token(jwtToken)
                .build();
        // Publish the RegistrationCompleteEvent
        publishRegistrationCompleteEvent(savedUser, httpRequest);
        return registrationResponse;
    }

    private void publishRegistrationCompleteEvent(User user, HttpServletRequest request) {
        String applicationUrl = "http://" + request.getServerName() + ":" +
                request.getServerPort() + '/' + request.getContextPath();
        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl));
    }

    public void saveUserVerificationToken(User theUser, String token) {
        var verificationToken = new VerificationToken(token,theUser);
        verificationTokenRepository.save(verificationToken);
    }
    @Transactional
    public String validateToken(String theToken) {
        VerificationToken token = verificationTokenRepository.findByToken(theToken);
        if (token == null) {
            throw new IllegalArgumentException("Invalid verification token");
        }
        
        User user = token.getUser();
        Calendar calendar = Calendar.getInstance();
        if ((token.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
            verificationTokenRepository.delete(token);
            throw new IllegalArgumentException("Token has expired");
        }
        
        user.setStatus(true);
        userRepository.save(user);
        verificationTokenRepository.delete(token); // Delete the token after successful validation
        return "valid";
    }
    @Transactional
    public Map<String, String> authenticate(AuthenticationRequest request) {
        // First check if user exists and is verified
        var user = repository.findByEmail(request.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (!user.isStatus()) {
            throw new IllegalArgumentException("Email not verified. Please verify your email first.");
        }

        // Then proceed with authentication
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return Collections.singletonMap("token", jwtToken);
    }
    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
    private String generateCustomIdentifier(Role role) {
        String randomString = RandomStringUtils.randomAlphanumeric(6); // Change length as needed
        return role.name() + "_" + randomString;
    }

}