package com.wallstreetrookie.backend.repository;

import com.wallstreetrookie.backend.model.StockPrice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StockPriceRepository extends MongoRepository<StockPrice, String> {

    List<StockPrice> findBySymbolOrderByTimestampDesc(String symbol);

    List<StockPrice> findByGameSessionId(String gameSessionId);
}