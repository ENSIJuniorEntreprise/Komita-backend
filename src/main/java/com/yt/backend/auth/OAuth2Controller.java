package com.yt.backend.auth;
import org.springframework.http.HttpStatus;

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

    @PostMapping("/loginWithGoogle")
    @ResponseBody
    public ResponseEntity<?> handleGoogleLogin(@RequestBody Map<String, String> requestBody) {
        // Expecting the Google OAuth2 token from the frontend
        String googleToken = requestBody.get("token");

        // Verify the Google token and authenticate the user
        try {
            AuthenticationResponse response = oAuth2Service.authenticateWithGoogleToken(googleToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: Invalid Google token");
        }
    }



}
