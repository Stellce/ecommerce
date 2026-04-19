package com.example.backend.security;

import com.example.backend.common.exception.AppException;
import com.example.backend.common.exception.ErrorCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CurrentUserService {

    public CustomUserPrincipal getCurrentPrincipal() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserPrincipal principal)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return principal;
    }

    public UUID getCurrentUserId() {
        return getCurrentPrincipal().getId();
    }

    public boolean isAdmin() {
        return getCurrentPrincipal().getRoles().contains("ADMIN");
    }
}
