package com.wallstreetrookie.backend.engine;

import com.wallstreetrookie.backend.dto.response.GameSessionResponse;
import com.wallstreetrookie.backend.mapper.GameSessionMapper;
import com.wallstreetrookie.backend.model.GameSession;
import com.wallstreetrookie.backend.model.enums.GameMode;
import com.wallstreetrookie.backend.model.enums.GameStatus;
import com.wallstreetrookie.backend.repository.GameSessionRepository;
import com.wallstreetrookie.backend.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TimeSkipService {

    private static final int TRADING_DAYS_PER_WEEK = 5;

    private final MarketSimulationService marketSimulationService;
    private final NewsService newsService;
    private final GameSessionRepository gameSessionRepository;
    private final PortfolioService portfolioService;
    private final GameSessionMapper gameSessionMapper;

    public GameSessionResponse skipWeeks(String sessionId, String playerId, int weeks) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session nicht gefunden"));

        if (session.getMode() != GameMode.SP) {
            throw new IllegalArgumentException("Zeit-Skip ist nur im Singleplayer möglich");
        }
        if (session.getStatus() != GameStatus.RUNNING) {
            throw new IllegalArgumentException("Session ist nicht aktiv");
        }
        if (!session.getPlayerIds().contains(playerId)) {
            throw new IllegalArgumentException("Spieler ist nicht Teil dieser Session");
        }

        for (int week = 0; week < weeks; week++) {
            newsService.generateNews(session.getId());

            for (int day = 0; day < TRADING_DAYS_PER_WEEK; day++) {
                marketSimulationService.simulateTick(session.getId());
            }

            session.setCurrentWeek(session.getCurrentWeek() + 1);

            if (session.getSettings().getTotalWeeks() > 0
                    && session.getCurrentWeek() > session.getSettings().getTotalWeeks()) {
                session.setStatus(GameStatus.ENDED);
                break;
            }
        }

        session = gameSessionRepository.save(session);

        for (String pid : session.getPlayerIds()) {
            portfolioService.getPortfolio(pid);
        }

        return gameSessionMapper.toResponse(session);
    }

}
