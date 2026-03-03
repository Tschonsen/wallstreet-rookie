package com.wallstreetrookie.backend.repository;

import com.wallstreetrookie.backend.model.StockModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends MongoRepository<StockModel, String> {

    Optional<StockModel> findBySymbol(String symbol);

    List<StockModel> findBySector(String sector);
}