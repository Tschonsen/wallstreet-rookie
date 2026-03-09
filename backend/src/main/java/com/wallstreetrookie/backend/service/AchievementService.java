package com.wallstreetrookie.backend.service;

import com.wallstreetrookie.backend.dto.response.AchievementResponse;
import com.wallstreetrookie.backend.model.enums.AchievementType;

import java.util.List;

public interface AchievementService {

    List<AchievementResponse> getPlayerAchievements(String playerId);

    boolean unlockAchievement(String playerId, AchievementType type, String description);

    String calculateRank(double returnPercent);
}
