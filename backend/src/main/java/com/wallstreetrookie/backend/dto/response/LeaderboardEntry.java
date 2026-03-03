package com.wallstreetrookie.backend.dto.response;

public record LeaderboardEntry(
        int rank,
        String username,
        double totalValue
) {}
