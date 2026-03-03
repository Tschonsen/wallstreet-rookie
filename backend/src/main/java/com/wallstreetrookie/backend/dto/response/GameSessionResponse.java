package com.wallstreetrookie.backend.dto.response;

import com.wallstreetrookie.backend.model.enums.GameMode;
import com.wallstreetrookie.backend.model.enums.GameStatus;

public record GameSessionResponse(
        String id,
        GameMode mode,
        int currentWeek,
        GameStatus status
) {}
