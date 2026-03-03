package com.wallstreetrookie.backend.repository;

import com.wallstreetrookie.backend.model.Trade;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TradeRepository extends MongoRepository<Trade, String> {

    List<Trade> findByPlayerId(String playerId);

    List<Trade> findByPlayerIdAndGameSessionId(String playerId, String gameSessionId);
}