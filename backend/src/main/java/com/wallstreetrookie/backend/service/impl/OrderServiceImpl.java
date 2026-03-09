package com.wallstreetrookie.backend.service.impl;

import com.wallstreetrookie.backend.dto.request.CreateOrderRequest;
import com.wallstreetrookie.backend.dto.response.OrderResponse;
import com.wallstreetrookie.backend.model.Order;
import com.wallstreetrookie.backend.model.enums.OrderStatus;
import com.wallstreetrookie.backend.model.enums.OrderType;
import com.wallstreetrookie.backend.repository.OrderRepository;
import com.wallstreetrookie.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public OrderResponse createOrder(String playerId, CreateOrderRequest request) {
        Order order = Order.builder()
                .playerId(playerId)
                .symbol(request.symbol())
                .orderType(request.orderType())
                .quantity(request.quantity())
                .targetPrice(request.targetPrice())
                .status(OrderStatus.OPEN)
                .createdAt(Instant.now())
                .build();

        order = orderRepository.save(order);
        log.info("Order erstellt: {} {} x {} @ {} für Spieler {}",
                request.orderType(), request.quantity(), request.symbol(), request.targetPrice(), playerId);

        return toResponse(order);
    }

    @Override
    public List<OrderResponse> getOpenOrders(String playerId) {
        return orderRepository.findByPlayerIdAndStatus(playerId, OrderStatus.OPEN)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void cancelOrder(String playerId, String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order nicht gefunden"));

        if (!order.getPlayerId().equals(playerId)) {
            throw new IllegalArgumentException("Keine Berechtigung für diese Order");
        }

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new IllegalArgumentException("Order kann nicht storniert werden (Status: " + order.getStatus() + ")");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        log.info("Order storniert: {} für Spieler {}", orderId, playerId);
    }

    @Override
    public int checkAndExecuteOrders(String symbol, double currentPrice) {
        List<Order> openOrders = orderRepository.findBySymbolAndStatus(symbol, OrderStatus.OPEN);
        int executed = 0;

        for (Order order : openOrders) {
            boolean shouldExecute = switch (order.getOrderType()) {
                case LIMIT_BUY -> currentPrice <= order.getTargetPrice();
                case LIMIT_SELL -> currentPrice >= order.getTargetPrice();
                case STOP_LOSS -> currentPrice <= order.getTargetPrice();
            };

            if (shouldExecute) {
                order.setStatus(OrderStatus.FILLED);
                order.setFilledAt(Instant.now());
                orderRepository.save(order);
                executed++;
                log.info("Order ausgeführt: {} {} x {} @ {} (aktueller Kurs: {})",
                        order.getOrderType(), order.getQuantity(), order.getSymbol(),
                        order.getTargetPrice(), currentPrice);
            }
        }

        return executed;
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getSymbol(),
                order.getOrderType(),
                order.getQuantity(),
                order.getTargetPrice(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getFilledAt()
        );
    }
}
