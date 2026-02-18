package com.wallstreetrookie.backend.model;

import com.wallstreetrookie.backend.model.enums.GameMode;
import com.wallstreetrookie.backend.model.enums.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("game_sessions")
public class GameSession {

    @Id
    private String id;

    private GameMode mode;

    private int currentWeek;

    private Instant startDate;

    private List<String> playerIds;

    private GameStatus status;

    private GameSettings settings;
}