package com.example.backend.testsupport;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

public final class SecurityTestUtils {

    private SecurityTestUtils() {
    }

    public static UserRequestPostProcessor adminUser() {
        return user("admin").authorities(new SimpleGrantedAuthority("ADMIN"));
    }

    public static UserRequestPostProcessor regularUser() {
        return user("user").authorities(new SimpleGrantedAuthority("USER"));
    }
}
