package com.wallstreetrookie.backend.dto.request;

import com.wallstreetrookie.backend.model.enums.OrderType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderRequest(
        @NotBlank(message = "Symbol darf nicht leer sein")
        String symbol,

        @NotNull(message = "OrderType darf nicht null sein")
        OrderType orderType,

        @Min(value = 1, message = "Menge muss mindestens 1 sein")
        int quantity,

        @Positive(message = "Zielpreis muss positiv sein")
        double targetPrice
) {}
