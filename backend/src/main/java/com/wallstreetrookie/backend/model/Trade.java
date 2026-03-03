package com.wallstreetrookie.backend.model;

import com.wallstreetrookie.backend.model.enums.TradeType;
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
@Document("trades")
public class Trade {

    @Id
    private String id;

    private String playerId;

    private String symbol;

    private TradeType type;

    private int quantity;

    private double price;

    private double total;

    private Instant timestamp;

    private String gameSessionId;
}