package com.wallstreetrookie.backend.repository;

import com.wallstreetrookie.backend.model.GameSession;
import com.wallstreetrookie.backend.model.enums.GameStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GameSessionRepository extends MongoRepository<GameSession, String> {

    List<GameSession> findByStatus(GameStatus status);

    List<GameSession> findByPlayerIdsContaining(String playerId);
}