package com.wallstreetrookie.backend.dto.response;

import java.time.Instant;

public record AuthResponse(
        String token,
        String username,
        Instant expiresAt
) {}
