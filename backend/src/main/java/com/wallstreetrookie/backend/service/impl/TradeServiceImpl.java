package com.wallstreetrookie.backend.service.impl;

import com.wallstreetrookie.backend.dto.request.TradeRequest;
import com.wallstreetrookie.backend.dto.response.TradeResponse;
import com.wallstreetrookie.backend.exception.InsufficientFundsException;
import com.wallstreetrookie.backend.exception.InsufficientSharesException;
import com.wallstreetrookie.backend.exception.InvalidTradeException;
import com.wallstreetrookie.backend.exception.StockNotFoundException;
import com.wallstreetrookie.backend.mapper.TradeMapper;
import com.wallstreetrookie.backend.model.Player;
import com.wallstreetrookie.backend.model.PortfolioEntry;
import com.wallstreetrookie.backend.model.StockPrice;
import com.wallstreetrookie.backend.model.Trade;
import com.wallstreetrookie.backend.model.enums.TradeType;
import com.wallstreetrookie.backend.repository.PlayerRepository;
import com.wallstreetrookie.backend.repository.StockPriceRepository;
import com.wallstreetrookie.backend.repository.StockRepository;
import com.wallstreetrookie.backend.repository.TradeRepository;
import com.wallstreetrookie.backend.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final PlayerRepository playerRepository;
    private final TradeRepository tradeRepository;
    private final StockRepository stockRepository;
    private final StockPriceRepository stockPriceRepository;
    private final TradeMapper tradeMapper;

    @Override
    public TradeResponse buyStock(String playerId, TradeRequest request) {
        validateQuantity(request.quantity());
        validateStockExists(request.symbol());

        double currentPrice = getCurrentPrice(request.symbol());
        double totalCost = currentPrice * request.quantity();

        Player player = getPlayer(playerId);
        if (player.getCash() < totalCost) {
            throw new InsufficientFundsException(
                    "Nicht genug Guthaben. Benötigt: " + totalCost + ", Verfügbar: " + player.getCash());
        }

        player.setCash(player.getCash() - totalCost);
        updatePortfolioForBuy(player, request.symbol(), request.quantity(), currentPrice);
        playerRepository.save(player);

        Trade trade = Trade.builder()
                .playerId(playerId)
                .symbol(request.symbol())
                .type(TradeType.BUY)
                .quantity(request.quantity())
                .price(currentPrice)
                .total(totalCost)
                .timestamp(Instant.now())
                .build();
        trade = tradeRepository.save(trade);

        return tradeMapper.toResponse(trade);
    }

    @Override
    public TradeResponse sellStock(String playerId, TradeRequest request) {
        validateQuantity(request.quantity());

        double currentPrice = getCurrentPrice(request.symbol());
        double totalRevenue = currentPrice * request.quantity();

        Player player = getPlayer(playerId);
        PortfolioEntry entry = findPortfolioEntry(player, request.symbol());

        if (entry == null || entry.getQuantity() < request.quantity()) {
            int owned = (entry != null) ? entry.getQuantity() : 0;
            throw new InsufficientSharesException(
                    "Nicht genug Aktien. Besitz: " + owned + ", Verkauf: " + request.quantity());
        }

        player.setCash(player.getCash() + totalRevenue);
        updatePortfolioForSell(player, entry, request.quantity());
        playerRepository.save(player);

        Trade trade = Trade.builder()
                .playerId(playerId)
                .symbol(request.symbol())
                .type(TradeType.SELL)
                .quantity(request.quantity())
                .price(currentPrice)
                .total(totalRevenue)
                .timestamp(Instant.now())
                .build();
        trade = tradeRepository.save(trade);

        return tradeMapper.toResponse(trade);
    }

    @Override
    public List<TradeResponse> getTradeHistory(String playerId) {
        return tradeRepository.findByPlayerId(playerId).stream()
                .map(tradeMapper::toResponse)
                .toList();
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new InvalidTradeException("Anzahl muss größer als 0 sein");
        }
    }

    private void validateStockExists(String symbol) {
        stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new StockNotFoundException("Aktie '" + symbol + "' nicht gefunden"));
    }

    private double getCurrentPrice(String symbol) {
        List<StockPrice> prices = stockPriceRepository.findBySymbolOrderByTimestampDesc(symbol);
        if (prices.isEmpty()) {
            throw new StockNotFoundException("Kein Kurs für Aktie '" + symbol + "' verfügbar");
        }
        return prices.getFirst().getPrice();
    }

    private Player getPlayer(String playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Spieler nicht gefunden"));
    }

    private PortfolioEntry findPortfolioEntry(Player player, String symbol) {
        if (player.getPortfolio() == null) return null;
        return player.getPortfolio().stream()
                .filter(e -> e.getSymbol().equals(symbol))
                .findFirst()
                .orElse(null);
    }

    private void updatePortfolioForBuy(Player player, String symbol, int quantity, double price) {
        if (player.getPortfolio() == null) {
            player.setPortfolio(new ArrayList<>());
        }

        PortfolioEntry existing = findPortfolioEntry(player, symbol);

        if (existing != null) {
            double newAverage = (existing.getQuantity() * existing.getAverageBuyPrice() + quantity * price)
                    / (existing.getQuantity() + quantity);
            existing.setQuantity(existing.getQuantity() + quantity);
            existing.setAverageBuyPrice(newAverage);
        } else {
            player.getPortfolio().add(PortfolioEntry.builder()
                    .symbol(symbol)
                    .quantity(quantity)
                    .averageBuyPrice(price)
                    .build());
        }
    }

    private void updatePortfolioForSell(Player player, PortfolioEntry entry, int quantity) {
        int remaining = entry.getQuantity() - quantity;
        if (remaining == 0) {
            player.getPortfolio().remove(entry);
        } else {
            entry.setQuantity(remaining);
        }
    }
}
