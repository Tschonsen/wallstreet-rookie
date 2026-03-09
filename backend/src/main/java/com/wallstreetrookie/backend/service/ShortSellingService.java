package com.wallstreetrookie.backend.service;

import com.wallstreetrookie.backend.dto.request.TradeRequest;
import com.wallstreetrookie.backend.dto.response.TradeResponse;

public interface ShortSellingService {

    TradeResponse openShort(String playerId, TradeRequest request);

    TradeResponse coverShort(String playerId, TradeRequest request);
}
