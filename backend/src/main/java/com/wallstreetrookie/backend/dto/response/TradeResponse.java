package com.wallstreetrookie.backend.dto.response;

import com.wallstreetrookie.backend.model.enums.TradeType;

import java.time.Instant;

public record TradeResponse(
        String symbol,
        TradeType type,
        int quantity,
        double price,
        double total,
        Instant timestamp
) {}
