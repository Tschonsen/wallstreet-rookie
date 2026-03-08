package com.wallstreetrookie.backend.model;

import com.wallstreetrookie.backend.model.enums.OrderStatus;
import com.wallstreetrookie.backend.model.enums.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("orders")
public class Order {

    @Id
    private String id;

    private String playerId;

    private String symbol;

    private OrderType orderType;

    private int quantity;

    private double targetPrice;

    private OrderStatus status;

    private Instant createdAt;

    private Instant filledAt;

    private String gameSessionId;
}
