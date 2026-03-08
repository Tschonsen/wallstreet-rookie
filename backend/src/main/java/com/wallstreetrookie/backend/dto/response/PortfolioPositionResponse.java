package com.wallstreetrookie.backend.dto.response;

public record PortfolioPositionResponse(
        String symbol,
        int quantity,
        double averageBuyPrice,
        double currentPrice,
        double positionValue,
        double profitLoss,
        double profitLossPercent
) {}
