package com.example.backend.auth;

import com.example.backend.auth.dto.request.LoginRequest;
import com.example.backend.auth.dto.request.RefreshTokenRequest;
import com.example.backend.auth.dto.request.RegisterRequest;
import com.example.backend.auth.dto.response.AuthResponse;
import com.example.backend.common.exception.*;
import com.example.backend.security.JwtService;
import com.example.backend.user.Role;
import com.example.backend.user.RoleRepository;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new EmailAlreadyExistsException();
        }

        String hashedPassword = bCryptPasswordEncoder.encode(registerRequest.password());
        Role role = roleRepository.findByName("USER")
                .orElseThrow(RoleNotFoundException::new);

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
                .orElseThrow(EmailNotExistsException::new);

        if (!bCryptPasswordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new InvalidCredentialException();
        }
        String jwt = jwtService.generateToken(user);

        RefreshToken refreshToken = createRefreshToken(user);
        refreshTokenRepository.save(refreshToken);
        return new AuthResponse(jwt, refreshToken.getToken());
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {

        RefreshToken oldRefreshToken = refreshTokenRepository.findByToken(refreshTokenRequest.refreshToken())
                .orElseThrow(InvalidTokenException::new);

        if (oldRefreshToken.isRevoked() || oldRefreshToken.getExpiresAt().isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
            throw new InvalidTokenException();
        }

        oldRefreshToken.setRevoked(true);
        refreshTokenRepository.save(oldRefreshToken);

        User user = oldRefreshToken.getUser();
        String jwt = jwtService.generateToken(user);

        RefreshToken refreshToken = createRefreshToken(user);
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(jwt, refreshToken.getToken());
    }

    private RefreshToken createRefreshToken(User user) {
        return new RefreshToken(user, UUID.randomUUID().toString().replace("-", ""), OffsetDateTime.now(ZoneOffset.UTC).plusDays(10));
    }
}
