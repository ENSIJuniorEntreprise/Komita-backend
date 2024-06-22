package com.yt.backend.auth;

import com.yt.backend.model.Adress;
import com.yt.backend.model.user.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "First name is required")
    private String firstname;

    @NotBlank(message = "Last name is required")
    private String lastname;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    //@NotBlank(message = "userAddress or Role is required")
    //you should first create an ADMIN to create address

    @Getter
    private Role role;
    private Adress userAddress;
}
