package com.wallstreetrookie.backend.controller;

import com.wallstreetrookie.backend.dto.request.SkipTimeRequest;
import com.wallstreetrookie.backend.dto.response.GameSessionResponse;
import com.wallstreetrookie.backend.engine.TimeSkipService;
import com.wallstreetrookie.backend.service.GameSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameSessionService gameSessionService;
    private final TimeSkipService timeSkipService;

    @PostMapping("/singleplayer")
    public ResponseEntity<GameSessionResponse> startSingleplayer(Authentication authentication) {
        String playerId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(gameSessionService.startSingleplayer(playerId));
    }

    @PostMapping("/multiplayer/join")
    public ResponseEntity<GameSessionResponse> joinMultiplayer(Authentication authentication) {
        String playerId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(gameSessionService.joinMultiplayer(playerId));
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<GameSessionResponse> getSession(@PathVariable String sessionId) {
        return ResponseEntity.ok(gameSessionService.getSession(sessionId));
    }

    @PostMapping("/session/{sessionId}/pause")
    public ResponseEntity<GameSessionResponse> pauseSession(
            @PathVariable String sessionId,
            Authentication authentication) {
        String playerId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(gameSessionService.pauseSession(sessionId, playerId));
    }

    @PostMapping("/session/{sessionId}/resume")
    public ResponseEntity<GameSessionResponse> resumeSession(
            @PathVariable String sessionId,
            Authentication authentication) {
        String playerId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(gameSessionService.resumeSession(sessionId, playerId));
    }

    @PostMapping("/session/{sessionId}/skip")
    public ResponseEntity<GameSessionResponse> skipTime(
            @PathVariable String sessionId,
            @Valid @RequestBody SkipTimeRequest request,
            Authentication authentication) {
        String playerId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(timeSkipService.skipWeeks(sessionId, playerId, request.weeks()));
    }
}
