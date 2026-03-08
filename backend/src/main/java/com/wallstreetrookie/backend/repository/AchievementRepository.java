package com.wallstreetrookie.backend.repository;

import com.wallstreetrookie.backend.model.Achievement;
import com.wallstreetrookie.backend.model.enums.AchievementType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AchievementRepository extends MongoRepository<Achievement, String> {

    List<Achievement> findByPlayerId(String playerId);

    boolean existsByPlayerIdAndAchievementType(String playerId, AchievementType achievementType);
}
