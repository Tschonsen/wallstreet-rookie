package com.wallstreetrookie.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioEntry {

    private String symbol;

    private int quantity;

    private double averageBuyPrice;

    private double shortEntryPrice;

    private double marginReserved;
}