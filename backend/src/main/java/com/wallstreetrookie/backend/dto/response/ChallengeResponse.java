package com.wallstreetrookie.backend.dto.response;

import com.wallstreetrookie.backend.model.enums.ChallengeType;

public record ChallengeResponse(
        String id,
        String name,
        String description,
        ChallengeType type,
        int durationWeeks,
        double targetReturn,
        String sectorRestriction
) {}
