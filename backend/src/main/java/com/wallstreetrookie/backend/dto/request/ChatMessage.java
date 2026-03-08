package com.wallstreetrookie.backend.dto.request;

import java.time.Instant;

public record ChatMessage(
        String username,
        String message,
        Instant timestamp
) {}
