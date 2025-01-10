package com.yt.backend.auth;

import com.yt.backend.model.user.User;
import com.yt.backend.model.user.Role;
import com.yt.backend.repository.UserRepository;
import com.yt.backend.repository.TokenRepository;
import com.yt.backend.token.Token;
import com.yt.backend.token.TokenType;
import com.yt.backend.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;

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

    public AuthenticationResponse authenticateWithGoogle(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");
        String picture = oAuth2User.getAttribute("picture");


        // Log the OAuth2 user details
        System.out.println("Received OAuth2 user: " + email);

        // Check if the user exists in the repository
        User existingUser = userRepository.findByEmail(email);
        if (existingUser == null) {
            System.out.println("Creating new user with email: " + email);
            existingUser = User.builder()
                    .email(email)
                    .firstname(firstName)
                    .lastname(lastName)
                    .role(Role.STANDARD_USER)
                    .status(true)
                    .customIdentifier(generateCustomIdentifier(Role.valueOf("STANDARD_USER")))
                    .profileImage(picture)
                    .build();
            userRepository.save(existingUser);
        }

        // Generate and save the JWT token
        String jwtToken = jwtService.generateToken(existingUser);
        revokeAllUserTokens(existingUser);
        saveUserToken(existingUser, jwtToken);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .firstname(existingUser.getFirstname())
                .lastname(existingUser.getLastname())
                .email(existingUser.getEmail())
                .build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService() {
        return userRequest -> {
            OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

            // Log the attributes of the OAuth2User
            System.out.println("OAuth2User attributes: " + oAuth2User.getAttributes());

            // Call the authentication method to process the OAuth2User
            authenticateWithGoogle(oAuth2User);

            return oAuth2User;
        };
    }

    private String generateCustomIdentifier(Role role) {
        String randomString = RandomStringUtils.randomAlphanumeric(6); // Change length as needed
        return role.name() + "_" + randomString;
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
}
