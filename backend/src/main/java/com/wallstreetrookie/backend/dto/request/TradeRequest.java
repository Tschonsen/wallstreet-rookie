package com.wallstreetrookie.backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record TradeRequest(
        @NotBlank(message = "Symbol darf nicht leer sein")
        String symbol,

        @Min(value = 1, message = "Anzahl muss mindestens 1 sein")
        int quantity
) {}
