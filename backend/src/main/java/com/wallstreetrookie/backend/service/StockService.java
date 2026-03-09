package com.wallstreetrookie.backend.service;

import com.wallstreetrookie.backend.dto.response.StockResponse;

import java.util.List;

public interface StockService {

    List<StockResponse> getAllStocks();

    StockResponse getStock(String symbol);
}
