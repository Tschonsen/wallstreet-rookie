package com.wallstreetrookie.backend.repository;

import com.wallstreetrookie.backend.model.Order;
import com.wallstreetrookie.backend.model.enums.OrderStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {

    List<Order> findByPlayerIdAndStatus(String playerId, OrderStatus status);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findBySymbolAndStatus(String symbol, OrderStatus status);
}
