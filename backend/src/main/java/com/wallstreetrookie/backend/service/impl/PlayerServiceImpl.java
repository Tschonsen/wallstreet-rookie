package com.wallstreetrookie.backend.service.impl;

import com.wallstreetrookie.backend.dto.request.LoginRequest;
import com.wallstreetrookie.backend.dto.request.RegisterRequest;
import com.wallstreetrookie.backend.dto.response.AuthResponse;
import com.wallstreetrookie.backend.model.Player;
import com.wallstreetrookie.backend.repository.PlayerRepository;
import com.wallstreetrookie.backend.security.JwtTokenProvider;
import com.wallstreetrookie.backend.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private static final double STARTING_CASH = 100_000.0;

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (playerRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username '" + request.username() + "' ist bereits vergeben");
        }

        Player player = Player.builder()
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .email(request.email())
                .cash(STARTING_CASH)
                .totalValue(STARTING_CASH)
                .portfolio(new ArrayList<>())
                .createdAt(Instant.now())
                .build();

        player = playerRepository.save(player);

        return buildAuthResponse(player.getId(), player.getUsername());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Player player = playerRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Ungültige Anmeldedaten"));

        if (!passwordEncoder.matches(request.password(), player.getPasswordHash())) {
            throw new IllegalArgumentException("Ungültige Anmeldedaten");
        }

        return buildAuthResponse(player.getId(), player.getUsername());
    }

    @Override
    public Player getCurrentPlayer(String userId) {
        return playerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Spieler nicht gefunden"));
    }

    private AuthResponse buildAuthResponse(String userId, String username) {
        String token = jwtTokenProvider.generateToken(userId);
        Instant expiresAt = jwtTokenProvider.getExpirationFromToken(token);
        return new AuthResponse(token, username, expiresAt);
    }
}
