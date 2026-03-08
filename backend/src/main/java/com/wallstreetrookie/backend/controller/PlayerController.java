package com.wallstreetrookie.backend.controller;

import com.wallstreetrookie.backend.dto.response.PlayerStatsResponse;
import com.wallstreetrookie.backend.dto.response.PortfolioResponse;
import com.wallstreetrookie.backend.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public class PlayerController {

    private final PortfolioService portfolioService;

    @GetMapping("/portfolio")
    public ResponseEntity<PortfolioResponse> getPortfolio(Authentication authentication) {
        String playerId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(portfolioService.getPortfolio(playerId));
    }

    @GetMapping("/stats")
    public ResponseEntity<PlayerStatsResponse> getPlayerStats(Authentication authentication) {
        String playerId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(portfolioService.getPlayerStats(playerId));
    }
}
