package com.wallstreetrookie.backend.controller;

import com.wallstreetrookie.backend.model.Player;
import com.wallstreetrookie.backend.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/player/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final PlayerRepository playerRepository;

    @GetMapping
    public ResponseEntity<List<String>> getWatchlist(Authentication auth) {
        Player player = getPlayer(auth);
        List<String> watchlist = player.getWatchlist() != null ? player.getWatchlist() : List.of();
        return ResponseEntity.ok(watchlist);
    }

    @PostMapping("/{symbol}")
    public ResponseEntity<List<String>> addToWatchlist(Authentication auth, @PathVariable String symbol) {
        Player player = getPlayer(auth);
        if (player.getWatchlist() == null) {
            player.setWatchlist(new ArrayList<>());
        }
        if (!player.getWatchlist().contains(symbol)) {
            player.getWatchlist().add(symbol);
            playerRepository.save(player);
        }
        return ResponseEntity.ok(player.getWatchlist());
    }

    @DeleteMapping("/{symbol}")
    public ResponseEntity<List<String>> removeFromWatchlist(Authentication auth, @PathVariable String symbol) {
        Player player = getPlayer(auth);
        if (player.getWatchlist() != null) {
            player.getWatchlist().remove(symbol);
            playerRepository.save(player);
        }
        return ResponseEntity.ok(player.getWatchlist() != null ? player.getWatchlist() : List.of());
    }

    private Player getPlayer(Authentication auth) {
        return playerRepository.findById(auth.getPrincipal().toString())
                .orElseThrow(() -> new IllegalArgumentException("Spieler nicht gefunden"));
    }
}
