package com.wallstreetrookie.backend.dto.response;

import com.wallstreetrookie.backend.model.PortfolioEntry;

import java.util.List;

public record PlayerResponse(
        String username,
        double cash,
        double totalValue,
        List<PortfolioEntry> portfolio
) {}
