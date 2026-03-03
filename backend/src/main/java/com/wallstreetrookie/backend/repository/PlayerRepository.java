package com.wallstreetrookie.backend.repository;

import com.wallstreetrookie.backend.model.Player;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PlayerRepository extends MongoRepository<Player, String> {

    Optional<Player> findByUsername(String username);

    Optional<Player> findByEmail(String email);

    boolean existsByUsername(String username);
}