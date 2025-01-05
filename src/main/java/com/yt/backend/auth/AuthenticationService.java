package com.yt.backend.auth;

import com.yt.backend.config.JwtService;
import com.yt.backend.event.RegistrationCompleteEvent;
import com.yt.backend.model.user.Role;
import com.yt.backend.model.user.User;
import com.yt.backend.repository.TokenRepository;
import com.yt.backend.repository.UserRepository;
import com.yt.backend.repository.VerificationTokenRepository;
import com.yt.backend.token.Token;
import com.yt.backend.token.TokenType;
import com.yt.backend.token.VerificationToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;

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


    public AuthenticationResponse register(RegisterRequest request,HttpServletRequest httpRequest) {
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .userAddress(request.getUserAddress())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        String customIdentifier = generateCustomIdentifier(user.getRole());
        user.setCustomIdentifier(customIdentifier);
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        saveUserToken(savedUser, jwtToken);
        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .token(jwtToken)
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .build();
        // Publish the RegistrationCompleteEvent
        publishRegistrationCompleteEvent(savedUser, httpRequest);
        return authenticationResponse;
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
    public String validateToken(String theToken) {
        VerificationToken token = verificationTokenRepository.findByToken(theToken);
        if(token == null){
            return "Invalid verification token";
        }
        User user = token.getUser();
        Calendar calendar = Calendar.getInstance();
        if ((token.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0){
            verificationTokenRepository.delete(token);
            return "Token already expired";
        }
        user.setStatus(true);
        userRepository.save(user);
        return "valid";
    }
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()

                )
        );
        var user = repository.findByEmail(request.getEmail());

        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .email(request.getEmail())
                .role(user.getRole())
                .build();
    }
    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser((int) user.getId());
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