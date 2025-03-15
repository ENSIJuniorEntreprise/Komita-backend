package com.yt.backend.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder 
public class RegisterResponse {
    private String firstname;
    private String lastname;
    private String token;

    // Constructor
    public RegisterResponse(String firstname, String lastname, String token) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.token = token;
    }

    // Getters and Setters
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}