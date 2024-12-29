package com.yt.backend.model.user;

import com.yt.backend.model.Adress;
import com.yt.backend.token.Token;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_table")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true) // Define custom identifier with unique constraint
    private String customIdentifier;

    private String firstname;
    private String lastname;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean status;

    private String password;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    private List<Token> tokens;

    @OneToOne(cascade = CascadeType.ALL)
    private Adress userAddress;

    // Add profileImage field here
    @Column(name = "profile_image")
    private String profileImage; // Field to store profile image URL or file path

    public String getProfileImage() {
        return profileImage;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    private String generateCustomIdentifier(Role role) {
        String randomString = RandomStringUtils.randomAlphanumeric(6); // Change length as needed
        return role.name() + "_" + randomString;
    }

    // Constructor with profileImage parameter
    public User(String firstname, String lastname, String email, Role role, String password, Adress userAddress,
            String profileImage) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.role = role;
        this.password = password;
        this.userAddress = userAddress;
        this.status = true; // Assuming new users are active by default
        this.customIdentifier = generateCustomIdentifier(role);
        this.profileImage = profileImage; // Initialize profile image
    }
}
