package com.wallstreetrookie.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document("stocks")
@NoArgsConstructor
@AllArgsConstructor
public class StockModel {
    @Id
    private String id;

    @Indexed(unique = true)
    private String symbol;

    private String name;

    private String sector;

    private String description;

    private double initialPrice;

    private double volatility;

    private double dividendYield;
}
