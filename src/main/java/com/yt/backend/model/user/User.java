package com.yt.backend.model.user;

import com.yt.backend.model.Adress;
import com.yt.backend.security.Token;

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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_table")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) 
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

    @Column(name = "profile_image", columnDefinition = "bytea")
    private byte[] profileImage;

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
    public boolean isStatus() {
        return this.status;
    }

    private String generateCustomIdentifier(Role role) {
        String randomString = RandomStringUtils.randomAlphanumeric(6); // Change length as needed
        return role.name() + "_" + randomString;
    }

    // Constructor with profileImage parameter
    public User(String firstname, String lastname, String email, Role role, String password, Adress userAddress,
            byte[] profileImage) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.role = role;
        this.password = password;
        this.userAddress = userAddress;
        this.status = true; 
        this.customIdentifier = generateCustomIdentifier(role);
        this.profileImage = profileImage; 
    }

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

}
