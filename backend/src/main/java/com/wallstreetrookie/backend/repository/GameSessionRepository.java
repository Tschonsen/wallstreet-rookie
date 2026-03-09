package com.wallstreetrookie.backend.repository;

import com.wallstreetrookie.backend.model.GameSession;
import com.wallstreetrookie.backend.model.enums.GameStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.wallstreetrookie.backend.model.enums.GameMode;

import java.util.List;
import java.util.Optional;

public interface GameSessionRepository extends MongoRepository<GameSession, String> {

    List<GameSession> findByStatus(GameStatus status);

    List<GameSession> findByPlayerIdsContaining(String playerId);

    Optional<GameSession> findByModeAndStatus(GameMode mode, GameStatus status);
}