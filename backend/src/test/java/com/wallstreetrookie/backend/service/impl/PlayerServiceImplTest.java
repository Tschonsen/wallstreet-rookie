package com.wallstreetrookie.backend.service.impl;

import com.wallstreetrookie.backend.dto.request.LoginRequest;
import com.wallstreetrookie.backend.dto.request.RegisterRequest;
import com.wallstreetrookie.backend.dto.response.AuthResponse;
import com.wallstreetrookie.backend.model.Player;
import com.wallstreetrookie.backend.repository.PlayerRepository;
import com.wallstreetrookie.backend.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceImplTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private PlayerServiceImpl playerService;

    @Test
    void register_shouldCreatePlayerWithStartingCash() {
        RegisterRequest request = new RegisterRequest("testuser", "password123", "test@mail.com");

        when(playerRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(playerRepository.save(any(Player.class))).thenAnswer(invocation -> {
            Player p = invocation.getArgument(0);
            p.setId("player1");
            return p;
        });
        when(jwtTokenProvider.generateToken("player1")).thenReturn("jwt-token");
        when(jwtTokenProvider.getExpirationFromToken("jwt-token")).thenReturn(Instant.now().plusSeconds(86400));

        AuthResponse response = playerService.register(request);

        assertNotNull(response);
        assertEquals("testuser", response.username());
        assertEquals("jwt-token", response.token());
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    void register_duplicateUsername_shouldThrowException() {
        RegisterRequest request = new RegisterRequest("existinguser", "password123", "test@mail.com");
        when(playerRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> playerService.register(request));
        verify(playerRepository, never()).save(any());
    }

    @Test
    void login_validCredentials_shouldReturnAuthResponse() {
        LoginRequest request = new LoginRequest("testuser", "password123");
        Player player = Player.builder()
                .id("player1")
                .username("testuser")
                .passwordHash("hashedPassword")
                .cash(100_000.0)
                .portfolio(new ArrayList<>())
                .build();

        when(playerRepository.findByUsername("testuser")).thenReturn(Optional.of(player));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateToken("player1")).thenReturn("jwt-token");
        when(jwtTokenProvider.getExpirationFromToken("jwt-token")).thenReturn(Instant.now().plusSeconds(86400));

        AuthResponse response = playerService.login(request);

        assertNotNull(response);
        assertEquals("testuser", response.username());
        assertEquals("jwt-token", response.token());
    }

    @Test
    void login_wrongPassword_shouldThrowException() {
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");
        Player player = Player.builder()
                .id("player1")
                .username("testuser")
                .passwordHash("hashedPassword")
                .build();

        when(playerRepository.findByUsername("testuser")).thenReturn(Optional.of(player));
        when(passwordEncoder.matches("wrongpassword", "hashedPassword")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> playerService.login(request));
    }

    @Test
    void login_unknownUser_shouldThrowException() {
        LoginRequest request = new LoginRequest("unknown", "password123");
        when(playerRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> playerService.login(request));
    }

    @Test
    void getCurrentPlayer_existingPlayer_shouldReturnPlayer() {
        Player player = Player.builder()
                .id("player1")
                .username("testuser")
                .cash(100_000.0)
                .build();

        when(playerRepository.findById("player1")).thenReturn(Optional.of(player));

        Player result = playerService.getCurrentPlayer("player1");

        assertEquals("testuser", result.getUsername());
        assertEquals(100_000.0, result.getCash());
    }

    @Test
    void getCurrentPlayer_unknownPlayer_shouldThrowException() {
        when(playerRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> playerService.getCurrentPlayer("unknown"));
    }
}
