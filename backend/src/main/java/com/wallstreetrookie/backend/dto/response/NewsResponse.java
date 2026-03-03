package com.wallstreetrookie.backend.dto.response;

import java.time.Instant;
import java.util.List;

public record NewsResponse(
        String title,
        String content,
        List<String> affectedSymbols,
        double impact,
        Instant timestamp
) {}
