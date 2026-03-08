package com.wallstreetrookie.backend.service.impl;

import com.wallstreetrookie.backend.dto.response.PlayerStatsResponse;
import com.wallstreetrookie.backend.dto.response.PortfolioPositionResponse;
import com.wallstreetrookie.backend.dto.response.PortfolioResponse;
import com.wallstreetrookie.backend.exception.StockNotFoundException;
import com.wallstreetrookie.backend.model.Player;
import com.wallstreetrookie.backend.model.PortfolioEntry;
import com.wallstreetrookie.backend.model.StockPrice;
import com.wallstreetrookie.backend.repository.PlayerRepository;
import com.wallstreetrookie.backend.repository.StockPriceRepository;
import com.wallstreetrookie.backend.repository.TradeRepository;
import com.wallstreetrookie.backend.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private static final double STARTING_CASH = 100_000.0;

    private final PlayerRepository playerRepository;
    private final StockPriceRepository stockPriceRepository;
    private final TradeRepository tradeRepository;

    @Override
    public PortfolioResponse getPortfolio(String playerId) {
        Player player = getPlayer(playerId);

        List<PortfolioPositionResponse> positions = new ArrayList<>();
        double portfolioValue = 0;

        if (player.getPortfolio() != null) {
            for (PortfolioEntry entry : player.getPortfolio()) {
                double currentPrice = getCurrentPrice(entry.getSymbol());
                double positionValue = currentPrice * entry.getQuantity();
                double profitLoss = (currentPrice - entry.getAverageBuyPrice()) * entry.getQuantity();
                double profitLossPercent = ((currentPrice - entry.getAverageBuyPrice()) / entry.getAverageBuyPrice()) * 100;

                positions.add(new PortfolioPositionResponse(
                        entry.getSymbol(),
                        entry.getQuantity(),
                        entry.getAverageBuyPrice(),
                        currentPrice,
                        positionValue,
                        profitLoss,
                        profitLossPercent
                ));

                portfolioValue += positionValue;
            }
        }

        double totalValue = player.getCash() + portfolioValue;

        player.setTotalValue(totalValue);
        playerRepository.save(player);

        return new PortfolioResponse(player.getCash(), portfolioValue, totalValue, positions);
    }

    @Override
    public PlayerStatsResponse getPlayerStats(String playerId) {
        Player player = getPlayer(playerId);

        double portfolioValue = 0;
        if (player.getPortfolio() != null) {
            for (PortfolioEntry entry : player.getPortfolio()) {
                double currentPrice = getCurrentPrice(entry.getSymbol());
                portfolioValue += currentPrice * entry.getQuantity();
            }
        }

        double totalValue = player.getCash() + portfolioValue;
        double totalProfitLoss = totalValue - STARTING_CASH;
        double totalProfitLossPercent = (totalProfitLoss / STARTING_CASH) * 100;
        long tradeCount = tradeRepository.findByPlayerId(playerId).size();

        player.setTotalValue(totalValue);
        playerRepository.save(player);

        return new PlayerStatsResponse(
                player.getUsername(),
                player.getCash(),
                totalValue,
                totalProfitLoss,
                totalProfitLossPercent,
                tradeCount
        );
    }

    private Player getPlayer(String playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Spieler nicht gefunden"));
    }

    private double getCurrentPrice(String symbol) {
        List<StockPrice> prices = stockPriceRepository.findBySymbolOrderByTimestampDesc(symbol);
        if (prices.isEmpty()) {
            throw new StockNotFoundException("Kein Kurs für Aktie '" + symbol + "' verfügbar");
        }
        return prices.getFirst().getPrice();
    }
}
