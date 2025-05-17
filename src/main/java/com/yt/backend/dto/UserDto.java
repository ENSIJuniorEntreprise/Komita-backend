package com.yt.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// Add phoneNumber field to your UserDto class
public class UserDto {
    private Long id;
    private String customIdentifier;
    private String firstname;
    private String lastname;
    private String email;
    private String role;
    private boolean status;
    private boolean enabled;
    private String username;
    private AddressDto userAddress;
    private String profileImage; 
    private String phoneNumber;
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
