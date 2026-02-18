package com.wallstreetrookie.backend.model;

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
@Document("stock_prices")
public class StockPrice {

    @Id
    private String id;

    private String symbol;

    private double price;

    private double change;

    private double changePercent;

    private Instant timestamp;

    private String gameSessionId;
}