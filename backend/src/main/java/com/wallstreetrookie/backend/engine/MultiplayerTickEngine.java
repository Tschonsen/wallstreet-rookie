package com.wallstreetrookie.backend.engine;

import com.wallstreetrookie.backend.dto.response.LeaderboardEntry;
import com.wallstreetrookie.backend.dto.response.StockResponse;
import com.wallstreetrookie.backend.model.GameSession;
import com.wallstreetrookie.backend.model.enums.GameMode;
import com.wallstreetrookie.backend.model.enums.GameStatus;
import com.wallstreetrookie.backend.repository.GameSessionRepository;
import com.wallstreetrookie.backend.service.LeaderboardService;
import com.wallstreetrookie.backend.service.PortfolioService;
import com.wallstreetrookie.backend.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class MultiplayerTickEngine {

    private final MarketSimulationService marketSimulationService;
    private final NewsService newsService;
    private final StockService stockService;
    private final PortfolioService portfolioService;
    private final LeaderboardService leaderboardService;
    private final GameSessionRepository gameSessionRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final Random random = new Random();

    private int tickCount = 0;

    @Scheduled(fixedRate = 30000)
    public void tick() {
        GameSession mpSession = gameSessionRepository
                .findByModeAndStatus(GameMode.MP, GameStatus.RUNNING)
                .orElse(null);

        if (mpSession == null) {
            return;
        }

        tickCount++;
        log.debug("MP Tick #{} für Session {}", tickCount, mpSession.getId());

        // Kurse simulieren
        marketSimulationService.simulateTick(mpSession.getId());

        // News generieren (~alle 5 Minuten = alle 10 Ticks)
        if (tickCount % 10 == 0) {
            newsService.generateNews(mpSession.getId());
            var news = newsService.generateNews(mpSession.getId());
            news.forEach(n -> messagingTemplate.convertAndSend("/topic/market/news", n));
        }

        // Kurse an alle Clients pushen
        List<StockResponse> stocks = stockService.getAllStocks();
        messagingTemplate.convertAndSend("/topic/market/prices", stocks);

        // Portfolio-Werte für alle Spieler aktualisieren
        for (String playerId : mpSession.getPlayerIds()) {
            try {
                var portfolio = portfolioService.getPortfolio(playerId);
                messagingTemplate.convertAndSendToUser(playerId, "/queue/portfolio", portfolio);
            } catch (Exception e) {
                log.warn("Portfolio-Update für Spieler {} fehlgeschlagen", playerId);
            }
        }

        // Leaderboard aktualisieren und pushen
        List<LeaderboardEntry> leaderboard = leaderboardService.getLeaderboard();
        messagingTemplate.convertAndSend("/topic/leaderboard", leaderboard);

        // Woche erhöhen (alle 35 Ticks = ~1 Woche bei 5 Tagen/Woche, 30s/Tick)
        if (tickCount % 35 == 0) {
            mpSession.setCurrentWeek(mpSession.getCurrentWeek() + 1);
            gameSessionRepository.save(mpSession);
        }
    }
}
