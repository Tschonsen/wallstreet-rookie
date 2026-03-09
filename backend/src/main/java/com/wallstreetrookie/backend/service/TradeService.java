package com.wallstreetrookie.backend.service;

import com.wallstreetrookie.backend.dto.request.TradeRequest;
import com.wallstreetrookie.backend.dto.response.TradeResponse;

import java.util.List;

public interface TradeService {

    TradeResponse buyStock(String playerId, TradeRequest request);

    TradeResponse sellStock(String playerId, TradeRequest request);

    List<TradeResponse> getTradeHistory(String playerId);
}
