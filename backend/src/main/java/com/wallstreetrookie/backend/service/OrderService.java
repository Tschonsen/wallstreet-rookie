package com.wallstreetrookie.backend.service;

import com.wallstreetrookie.backend.dto.request.CreateOrderRequest;
import com.wallstreetrookie.backend.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(String playerId, CreateOrderRequest request);

    List<OrderResponse> getOpenOrders(String playerId);

    void cancelOrder(String playerId, String orderId);

    int checkAndExecuteOrders(String symbol, double currentPrice);
}
