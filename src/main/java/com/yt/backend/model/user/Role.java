package com.yt.backend.model.user;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;

import static com.yt.backend.model.user.Permission.*;
@RequiredArgsConstructor
public enum Role {

    //standard user
    STANDARD_USER(Collections.emptySet()),

    //the system admin

    ADMIN(
            Set.of(
                    ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    ADMIN_CREATE
            )

    ),
    //the professional user
    PROFESSIONAL(Set.of(
            PROFESSIONAL_READ,
            PROFESSIONAL_UPDATE,
            PROFESSIONAL_CREATE,
            PROFESSIONAL_DELETE

    ));

    @Getter
    private final Set<Permission> permissions;
    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}