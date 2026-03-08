package com.wallstreetrookie.backend.dto.response;

import java.util.List;

public record PortfolioResponse(
        double cash,
        double portfolioValue,
        double totalValue,
        List<PortfolioPositionResponse> positions
) {}
