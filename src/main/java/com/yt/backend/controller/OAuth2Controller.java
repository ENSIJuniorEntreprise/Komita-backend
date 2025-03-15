package com.yt.backend.controller;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.yt.backend.dto.AuthenticationResponse;
import com.yt.backend.service.OAuth2Service;

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

    @PostMapping("/loginWithGoogle")
@ResponseBody
public ResponseEntity<?> handleGoogleLogin(@RequestBody Map<String, String> requestBody) {
    String googleToken = requestBody.get("token");

    try {
        AuthenticationResponse response = oAuth2Service.authenticateWithGoogleToken(googleToken);

        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist. Please sign up first.");
        }

        return ResponseEntity.ok(response);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: Invalid Google token.");
    }
}


    @PostMapping("/signUpWithGoogle")
@ResponseBody
public ResponseEntity<?> handleGoogleSignUp(@RequestBody Map<String, String> requestBody) {
    String googleToken = requestBody.get("token");

    try {
        AuthenticationResponse response = oAuth2Service.registerWithGoogleToken(googleToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sign-up failed: " + e.getMessage());
    }
}




}
