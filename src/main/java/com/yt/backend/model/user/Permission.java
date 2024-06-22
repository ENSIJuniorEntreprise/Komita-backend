package com.yt.backend.model.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum Permission {
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
    PROFESSIONAL_READ("professional:read"),
    PROFESSIONAL_UPDATE("professional:update"),
    PROFESSIONAL_CREATE("professional:create"),
    PROFESSIONAL_DELETE("professional:delete");
    private final String permission;
}
