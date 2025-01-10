package com.yt.backend.auth;

import com.yt.backend.auth.OAuth2Service;
import com.yt.backend.auth.AuthenticationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Controller
@RequestMapping("/api/v1/oauth2")
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;

    // Constructor injecting OAuth2Service
    public OAuth2Controller(OAuth2Service oAuth2Service) {
        this.oAuth2Service = oAuth2Service;
    }

    // Endpoint to handle OAuth2 login success
    @GetMapping("/login/success")
    @ResponseBody
    public ResponseEntity<?> handleOAuth2Login() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Principal: " + principal);

        if (principal instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) principal;
            System.out.println("OAuth2 principal: " + oAuth2User.getAttributes());
            AuthenticationResponse response = oAuth2Service.authenticateWithGoogle(oAuth2User);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: Not an OAuth2User");
        }
    }



    // Endpoint for getting the profile information of the authenticated user
    @GetMapping("/profile")
    @ResponseBody
    public ResponseEntity<?> profile(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        // Return the profile information of the authenticated user
        return ResponseEntity.ok(principal.getAttributes());
    }

    // Endpoint to handle OAuth2 login failure
    @GetMapping("/login/failure")
    @ResponseBody
    public ResponseEntity<?> handleOAuth2LoginFailure() {
        // Log the failure or additional information if needed
        System.out.println("OAuth2 login failed");

        // Return a response to the client
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Authentication failed: Unable to complete OAuth2 login.");
    }



}
