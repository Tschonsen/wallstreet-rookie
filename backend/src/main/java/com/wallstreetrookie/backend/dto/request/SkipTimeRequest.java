package com.wallstreetrookie.backend.dto.request;

import jakarta.validation.constraints.Min;

public record SkipTimeRequest(
        @Min(value = 1, message = "Anzahl Wochen muss mindestens 1 sein")
        int weeks
) {}
