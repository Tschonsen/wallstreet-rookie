package com.wallstreetrookie.backend.model;

import com.wallstreetrookie.backend.model.enums.AchievementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("achievements")
public class Achievement {

    @Id
    private String id;

    private String playerId;

    private AchievementType achievementType;

    private String description;

    private Instant unlockedAt;
}
