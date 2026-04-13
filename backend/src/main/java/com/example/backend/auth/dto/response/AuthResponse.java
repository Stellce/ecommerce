package com.example.backend.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record AuthResponse(
        @JsonProperty("token")
        @NotBlank
        String jwt,

        @JsonProperty("refresh_token")
        @NotBlank
        String refreshToken
) {}