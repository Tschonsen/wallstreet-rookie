package com.wallstreetrookie.backend.controller;

import com.wallstreetrookie.backend.dto.request.LoginRequest;
import com.wallstreetrookie.backend.dto.request.RegisterRequest;
import com.wallstreetrookie.backend.dto.response.AuthResponse;
import com.wallstreetrookie.backend.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Registrierung und Login")
public class AuthController {

    private final PlayerService playerService;

    @Operation(summary = "Neuen Spieler registrieren")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(playerService.register(request));
    }

    @Operation(summary = "Spieler einloggen")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(playerService.login(request));
    }
}
