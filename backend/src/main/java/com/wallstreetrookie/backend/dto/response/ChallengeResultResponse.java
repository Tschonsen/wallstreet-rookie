package com.wallstreetrookie.backend.dto.response;

public record ChallengeResultResponse(
        String challengeName,
        double startValue,
        double endValue,
        double returnPercent,
        String rank
) {}
