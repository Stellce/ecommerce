package com.example.backend.auth;

import com.example.backend.auth.dto.request.LoginRequest;
import com.example.backend.auth.dto.request.RefreshTokenRequest;
import com.example.backend.auth.dto.request.RegisterRequest;
import com.example.backend.auth.dto.response.AuthResponse;
import com.example.backend.common.exception.AppException;
import com.example.backend.common.exception.ErrorCode;
import com.example.backend.config.JwtProperties;
import com.example.backend.order.OrderRepository;
import com.example.backend.security.CurrentUserService;
import com.example.backend.security.CustomUserPrincipal;
import com.example.backend.security.JwtService;
import com.example.backend.user.Role;
import com.example.backend.user.RoleRepository;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrderRepository orderRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;
    private final CurrentUserService currentUserService;

    private final JwtProperties jwtProperties;

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        String hashedPassword = bCryptPasswordEncoder.encode(registerRequest.password());
        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        User user = new User(registerRequest.email(), hashedPassword, Set.of(role));
        userRepository.save(user);

        String jwt = jwtService.generateToken(user);
        RefreshToken refreshToken = createRefreshToken(user);
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(jwt, refreshToken.getToken());
    }

    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (!bCryptPasswordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
        String jwt = jwtService.generateToken(user);

        RefreshToken refreshToken = createRefreshToken(user);
        refreshTokenRepository.save(refreshToken);
        return new AuthResponse(jwt, refreshToken.getToken());
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {

        RefreshToken oldRefreshToken = refreshTokenRepository.findByToken(refreshTokenRequest.refreshToken())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        if (oldRefreshToken.isRevoked() || oldRefreshToken.getExpiresAt().isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        oldRefreshToken.setRevoked(true);
        refreshTokenRepository.save(oldRefreshToken);

        User user = oldRefreshToken.getUser();
        String jwt = jwtService.generateToken(user);

        RefreshToken refreshToken = createRefreshToken(user);
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(jwt, refreshToken.getToken());
    }

    public boolean canAccessOrder(UUID orderId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserPrincipal principal)) {
            return false;
        }

        if (currentUserService.isAdmin()) return true;

        return orderRepository.existsByIdAndUserId(orderId, principal.getId());
    }

    private RefreshToken createRefreshToken(User user) {
        return new RefreshToken(user, UUID.randomUUID().toString().replace("-", ""), OffsetDateTime.now(ZoneOffset.UTC).plus(jwtProperties.refreshExpiration()));
    }
}
