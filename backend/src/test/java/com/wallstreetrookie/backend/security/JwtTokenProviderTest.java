package com.wallstreetrookie.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(
                "wallstreet-rookie-jwt-secret-key-min-32-bytes!",
                86400000 // 24 hours
        );
    }

    @Test
    void generateToken_shouldReturnNonNullToken() {
        String token = jwtTokenProvider.generateToken("user123");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void getUserIdFromToken_shouldReturnCorrectUserId() {
        String userId = "user123";
        String token = jwtTokenProvider.generateToken(userId);

        assertEquals(userId, jwtTokenProvider.getUserIdFromToken(token));
    }

    @Test
    void getExpirationFromToken_shouldReturnFutureDate() {
        String token = jwtTokenProvider.generateToken("user123");
        Instant expiration = jwtTokenProvider.getExpirationFromToken(token);

        assertTrue(expiration.isAfter(Instant.now()));
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        String token = jwtTokenProvider.generateToken("user123");
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidToken() {
        assertFalse(jwtTokenProvider.validateToken("invalid.token.here"));
    }

    @Test
    void validateToken_shouldReturnFalseForEmptyToken() {
        assertFalse(jwtTokenProvider.validateToken(""));
    }

    @Test
    void generateToken_differentUserIds_shouldProduceDifferentTokens() {
        String token1 = jwtTokenProvider.generateToken("user1");
        String token2 = jwtTokenProvider.generateToken("user2");

        assertNotEquals(token1, token2);
    }
}
