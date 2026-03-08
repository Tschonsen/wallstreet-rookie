package com.wallstreetrookie.backend.service;

import com.wallstreetrookie.backend.dto.response.PlayerStatsResponse;
import com.wallstreetrookie.backend.dto.response.PortfolioResponse;

public interface PortfolioService {

    PortfolioResponse getPortfolio(String playerId);

    PlayerStatsResponse getPlayerStats(String playerId);
}
