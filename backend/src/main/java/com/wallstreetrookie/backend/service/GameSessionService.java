package com.wallstreetrookie.backend.service;

import com.wallstreetrookie.backend.dto.response.GameSessionResponse;

public interface GameSessionService {

    GameSessionResponse startSingleplayer(String playerId);

    GameSessionResponse joinMultiplayer(String playerId);

    GameSessionResponse getSession(String sessionId);

    GameSessionResponse pauseSession(String sessionId, String playerId);

    GameSessionResponse resumeSession(String sessionId, String playerId);
}
