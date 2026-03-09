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
@Document("dividend_payments")
public class DividendPayment {

    @Id
    private String id;

    private String playerId;

    private String symbol;

    private double amount;

    private Instant timestamp;

    private String gameSessionId;
}
