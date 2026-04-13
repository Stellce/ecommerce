package com.example.backend.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CustomUserPrincipal {
    private UUID id;
    private String email;
    private Set<String> roles;
}
