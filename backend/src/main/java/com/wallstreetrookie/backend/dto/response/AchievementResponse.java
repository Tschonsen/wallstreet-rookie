package com.wallstreetrookie.backend.dto.response;

import com.wallstreetrookie.backend.model.enums.AchievementType;

import java.time.Instant;

public record AchievementResponse(
        String id,
        AchievementType achievementType,
        String description,
        Instant unlockedAt
) {}
