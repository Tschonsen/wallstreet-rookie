package com.wallstreetrookie.backend.service.impl;

import com.wallstreetrookie.backend.dto.response.AchievementResponse;
import com.wallstreetrookie.backend.model.Achievement;
import com.wallstreetrookie.backend.model.enums.AchievementType;
import com.wallstreetrookie.backend.repository.AchievementRepository;
import com.wallstreetrookie.backend.service.AchievementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository achievementRepository;

    @Override
    public List<AchievementResponse> getPlayerAchievements(String playerId) {
        return achievementRepository.findByPlayerId(playerId)
                .stream()
                .map(a -> new AchievementResponse(a.getId(), a.getAchievementType(), a.getDescription(), a.getUnlockedAt()))
                .toList();
    }

    @Override
    public boolean unlockAchievement(String playerId, AchievementType type, String description) {
        if (achievementRepository.existsByPlayerIdAndAchievementType(playerId, type)) {
            return false;
        }

        Achievement achievement = Achievement.builder()
                .playerId(playerId)
                .achievementType(type)
                .description(description)
                .unlockedAt(Instant.now())
                .build();

        achievementRepository.save(achievement);
        log.info("Achievement freigeschaltet: {} für Spieler {}", type, playerId);
        return true;
    }

    @Override
    public String calculateRank(double returnPercent) {
        if (returnPercent > 100) return "S";
        if (returnPercent > 50) return "A";
        if (returnPercent > 20) return "B";
        if (returnPercent > 0) return "C";
        if (returnPercent > -90) return "D";
        return "F";
    }
}
