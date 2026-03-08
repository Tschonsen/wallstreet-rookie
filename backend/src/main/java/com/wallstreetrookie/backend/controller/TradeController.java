package com.wallstreetrookie.backend.controller;

import com.wallstreetrookie.backend.dto.request.TradeRequest;
import com.wallstreetrookie.backend.dto.response.TradeResponse;
import com.wallstreetrookie.backend.service.TradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @PostMapping("/buy")
    public ResponseEntity<TradeResponse> buyStock(
            Authentication authentication,
            @Valid @RequestBody TradeRequest request) {
        String playerId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(tradeService.buyStock(playerId, request));
    }

    @PostMapping("/sell")
    public ResponseEntity<TradeResponse> sellStock(
            Authentication authentication,
            @Valid @RequestBody TradeRequest request) {
        String playerId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(tradeService.sellStock(playerId, request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<TradeResponse>> getTradeHistory(Authentication authentication) {
        String playerId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(tradeService.getTradeHistory(playerId));
    }
}
