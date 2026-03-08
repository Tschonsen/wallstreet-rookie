package com.wallstreetrookie.backend.dto.response;

import com.wallstreetrookie.backend.model.enums.OrderStatus;
import com.wallstreetrookie.backend.model.enums.OrderType;

import java.time.Instant;

public record OrderResponse(
        String id,
        String symbol,
        OrderType orderType,
        int quantity,
        double targetPrice,
        OrderStatus status,
        Instant createdAt,
        Instant filledAt
) {}
