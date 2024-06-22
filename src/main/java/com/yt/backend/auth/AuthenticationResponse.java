package com.yt.backend.auth;

import com.yt.backend.model.user.Role;
import com.yt.backend.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse  {
    private String firstname;
    private String lastname;
    private String token;
    private String email;
    private Role role;
}
