package com.wallstreetrookie.backend.dto.response;

public record PlayerStatsResponse(
        String username,
        double cash,
        double totalValue,
        double totalProfitLoss,
        double totalProfitLossPercent,
        long tradeCount
) {}
