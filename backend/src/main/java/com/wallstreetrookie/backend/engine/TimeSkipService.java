package com.wallstreetrookie.backend.engine;

import com.wallstreetrookie.backend.dto.response.GameSessionResponse;
import com.wallstreetrookie.backend.mapper.GameSessionMapper;
import com.wallstreetrookie.backend.model.GameSession;
import com.wallstreetrookie.backend.model.News;
import com.wallstreetrookie.backend.model.StockModel;
import com.wallstreetrookie.backend.model.enums.GameMode;
import com.wallstreetrookie.backend.model.enums.GameStatus;
import com.wallstreetrookie.backend.repository.GameSessionRepository;
import com.wallstreetrookie.backend.repository.NewsRepository;
import com.wallstreetrookie.backend.repository.PlayerRepository;
import com.wallstreetrookie.backend.repository.StockRepository;
import com.wallstreetrookie.backend.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TimeSkipService {

    private static final int TRADING_DAYS_PER_WEEK = 5;

    private static final List<String> NEWS_TEMPLATES_POSITIVE = List.of(
            "%s meldet Rekordquartal",
            "Starkes Wachstum im %s-Sektor erwartet",
            "%s übertrifft Analystenerwartungen",
            "Neue Partnerschaft stärkt %s",
            "Durchbruch bei %s: Aktienkurs steigt"
    );

    private static final List<String> NEWS_TEMPLATES_NEGATIVE = List.of(
            "%s unter Druck: Gewinnwarnung",
            "Regulierung trifft %s-Sektor hart",
            "%s verliert Marktanteile",
            "Skandal bei %s erschüttert Anleger",
            "Rückrufaktion belastet %s"
    );

    private final MarketSimulationService marketSimulationService;
    private final GameSessionRepository gameSessionRepository;
    private final StockRepository stockRepository;
    private final NewsRepository newsRepository;
    private final PlayerRepository playerRepository;
    private final PortfolioService portfolioService;
    private final GameSessionMapper gameSessionMapper;
    private final Random random = new Random();

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
            generateWeeklyNews(session.getId());

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

    private void generateWeeklyNews(String gameSessionId) {
        int newsCount = 1 + random.nextInt(3);
        List<StockModel> stocks = stockRepository.findAll();

        for (int i = 0; i < newsCount; i++) {
            StockModel stock = stocks.get(random.nextInt(stocks.size()));
            boolean positive = random.nextBoolean();

            List<String> templates = positive ? NEWS_TEMPLATES_POSITIVE : NEWS_TEMPLATES_NEGATIVE;
            String template = templates.get(random.nextInt(templates.size()));
            String title = String.format(template, stock.getName());

            double impact = (positive ? 1 : -1) * (0.02 + random.nextDouble() * 0.08);

            News news = News.builder()
                    .title(title)
                    .content(title + ".")
                    .affectedSymbols(List.of(stock.getSymbol()))
                    .affectedSector(stock.getSector())
                    .impact(impact)
                    .timestamp(Instant.now())
                    .gameSessionId(gameSessionId)
                    .build();

            newsRepository.save(news);
        }
    }
}
