package com.wallstreetrookie.backend.service.impl;

import com.wallstreetrookie.backend.dto.response.GameSessionResponse;
import com.wallstreetrookie.backend.mapper.GameSessionMapper;
import com.wallstreetrookie.backend.model.GameSession;
import com.wallstreetrookie.backend.model.GameSettings;
import com.wallstreetrookie.backend.model.StockModel;
import com.wallstreetrookie.backend.model.StockPrice;
import com.wallstreetrookie.backend.model.enums.GameMode;
import com.wallstreetrookie.backend.model.enums.GameStatus;
import com.wallstreetrookie.backend.repository.GameSessionRepository;
import com.wallstreetrookie.backend.repository.StockPriceRepository;
import com.wallstreetrookie.backend.repository.StockRepository;
import com.wallstreetrookie.backend.service.GameSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameSessionServiceImpl implements GameSessionService {

    private static final double DEFAULT_STARTING_CASH = 100_000.0;
    private static final int DEFAULT_TOTAL_WEEKS = 52;

    private final GameSessionRepository gameSessionRepository;
    private final StockRepository stockRepository;
    private final StockPriceRepository stockPriceRepository;
    private final GameSessionMapper gameSessionMapper;

    @Override
    public GameSessionResponse startSingleplayer(String playerId) {
        GameSession session = GameSession.builder()
                .mode(GameMode.SP)
                .currentWeek(1)
                .startDate(Instant.now())
                .playerIds(List.of(playerId))
                .status(GameStatus.RUNNING)
                .settings(GameSettings.builder()
                        .startingCash(DEFAULT_STARTING_CASH)
                        .totalWeeks(DEFAULT_TOTAL_WEEKS)
                        .newsEnabled(true)
                        .build())
                .build();

        session = gameSessionRepository.save(session);
        copyInitialPrices(session.getId());

        return gameSessionMapper.toResponse(session);
    }

    @Override
    public GameSessionResponse joinMultiplayer(String playerId) {
        GameSession session = gameSessionRepository
                .findByModeAndStatus(GameMode.MP, GameStatus.RUNNING)
                .orElseGet(() -> createMultiplayerSession(playerId));

        if (!session.getPlayerIds().contains(playerId)) {
            session.getPlayerIds().add(playerId);
            session = gameSessionRepository.save(session);
        }

        return gameSessionMapper.toResponse(session);
    }

    @Override
    public GameSessionResponse getSession(String sessionId) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session nicht gefunden"));
        return gameSessionMapper.toResponse(session);
    }

    @Override
    public GameSessionResponse pauseSession(String sessionId, String playerId) {
        GameSession session = getPlayerSession(sessionId, playerId);
        session.setStatus(GameStatus.PAUSED);
        session = gameSessionRepository.save(session);
        return gameSessionMapper.toResponse(session);
    }

    @Override
    public GameSessionResponse resumeSession(String sessionId, String playerId) {
        GameSession session = getPlayerSession(sessionId, playerId);
        session.setStatus(GameStatus.RUNNING);
        session = gameSessionRepository.save(session);
        return gameSessionMapper.toResponse(session);
    }

    private GameSession getPlayerSession(String sessionId, String playerId) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session nicht gefunden"));
        if (!session.getPlayerIds().contains(playerId)) {
            throw new IllegalArgumentException("Spieler ist nicht Teil dieser Session");
        }
        return session;
    }

    private GameSession createMultiplayerSession(String playerId) {
        GameSession session = GameSession.builder()
                .mode(GameMode.MP)
                .currentWeek(1)
                .startDate(Instant.now())
                .playerIds(new ArrayList<>(List.of(playerId)))
                .status(GameStatus.RUNNING)
                .settings(GameSettings.builder()
                        .startingCash(DEFAULT_STARTING_CASH)
                        .totalWeeks(0)
                        .newsEnabled(true)
                        .build())
                .build();

        session = gameSessionRepository.save(session);
        copyInitialPrices(session.getId());
        return session;
    }

    private void copyInitialPrices(String gameSessionId) {
        List<StockModel> stocks = stockRepository.findAll();
        List<StockPrice> prices = stocks.stream()
                .map(stock -> StockPrice.builder()
                        .symbol(stock.getSymbol())
                        .price(stock.getInitialPrice())
                        .change(0.0)
                        .changePercent(0.0)
                        .timestamp(Instant.now())
                        .gameSessionId(gameSessionId)
                        .build())
                .toList();
        stockPriceRepository.saveAll(prices);
    }
}
