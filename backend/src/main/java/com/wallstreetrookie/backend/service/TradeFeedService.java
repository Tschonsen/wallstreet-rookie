package com.wallstreetrookie.backend.service;

import com.wallstreetrookie.backend.dto.response.TradeFeedEntry;
import com.wallstreetrookie.backend.model.Player;
import com.wallstreetrookie.backend.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeFeedService {

    private static final double MIN_TRADE_VALUE = 5000.0;

    private final SimpMessagingTemplate messagingTemplate;
    private final PlayerRepository playerRepository;

    public void publishTrade(String playerId, String symbol, String action, int quantity, double total) {
        if (total < MIN_TRADE_VALUE) return;

        String username = playerRepository.findById(playerId)
                .map(Player::getUsername)
                .orElse("Unbekannt");

        TradeFeedEntry entry = new TradeFeedEntry(
                username, symbol, action, quantity, total, Instant.now());

        messagingTemplate.convertAndSend("/topic/trades/feed", entry);
        log.debug("Trade-Feed: {} {} {}x {} (${:.2f})", username, action, quantity, symbol, total);
    }
}
