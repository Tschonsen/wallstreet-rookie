package com.wallstreetrookie.backend.dto.response;

import java.time.Instant;

public record TradeFeedEntry(
        String username,
        String symbol,
        String action,
        int quantity,
        double total,
        Instant timestamp
) {}
