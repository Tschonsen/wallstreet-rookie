package com.wallstreetrookie.backend.repository;

import com.wallstreetrookie.backend.model.DividendPayment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DividendPaymentRepository extends MongoRepository<DividendPayment, String> {

    List<DividendPayment> findByPlayerId(String playerId);

    List<DividendPayment> findByPlayerIdAndGameSessionId(String playerId, String gameSessionId);
}
