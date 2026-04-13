package com.example.backend.security;

import com.example.backend.auth.Role;
import com.example.backend.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("roles", user.getRoles().stream()
                        .map(Role::getName)
                        .toList())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(10, ChronoUnit.MINUTES)))
                .signWith(getKey())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Authentication buildAuthentication(String token) {
        Claims claims = parseToken(token);

        UUID userId = UUID.fromString(claims.getSubject());
        String email = claims.get("email").toString();
        List<String> roles = claims.get("roles", List.class);

        CustomUserPrincipal principal = new CustomUserPrincipal(userId, email, new HashSet<>(roles));

        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(String.valueOf(role)))
                .toList();

        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                authorities
        );
    }

    private SecretKey getKey() {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        }
}
