package com.wallstreetrookie.backend.dto.response;

public record StockResponse(
        String symbol,
        String name,
        String sector,
        double price,
        double change,
        double changePercent
) {}
