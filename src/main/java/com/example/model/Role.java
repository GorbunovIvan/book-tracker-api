package com.example.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
