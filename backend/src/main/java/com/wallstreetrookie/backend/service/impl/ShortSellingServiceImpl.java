package com.wallstreetrookie.backend.service.impl;

import com.wallstreetrookie.backend.dto.request.TradeRequest;
import com.wallstreetrookie.backend.dto.response.TradeResponse;
import com.wallstreetrookie.backend.model.Player;
import com.wallstreetrookie.backend.model.PortfolioEntry;
import com.wallstreetrookie.backend.model.StockPrice;
import com.wallstreetrookie.backend.model.Trade;
import com.wallstreetrookie.backend.model.enums.TradeType;
import com.wallstreetrookie.backend.repository.PlayerRepository;
import com.wallstreetrookie.backend.repository.StockPriceRepository;
import com.wallstreetrookie.backend.repository.StockRepository;
import com.wallstreetrookie.backend.repository.TradeRepository;
import com.wallstreetrookie.backend.service.ShortSellingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortSellingServiceImpl implements ShortSellingService {

    private static final double MARGIN_REQUIREMENT = 0.5; // 50%

    private final PlayerRepository playerRepository;
    private final StockRepository stockRepository;
    private final StockPriceRepository stockPriceRepository;
    private final TradeRepository tradeRepository;

    @Override
    public TradeResponse openShort(String playerId, TradeRequest request) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Spieler nicht gefunden"));

        stockRepository.findBySymbol(request.symbol())
                .orElseThrow(() -> new IllegalArgumentException("Aktie nicht gefunden: " + request.symbol()));

        double currentPrice = getCurrentPrice(request.symbol());
        double totalValue = currentPrice * request.quantity();
        double marginRequired = totalValue * MARGIN_REQUIREMENT;

        if (player.getCash() < marginRequired) {
            throw new IllegalArgumentException(
                    String.format("Nicht genug Cash für Margin. Benötigt: $%.2f, Verfügbar: $%.2f",
                            marginRequired, player.getCash()));
        }

        // Margin reservieren
        player.setCash(player.getCash() - marginRequired);

        // Short-Position erstellen (negative Quantity)
        PortfolioEntry existingEntry = player.getPortfolio().stream()
                .filter(e -> e.getSymbol().equals(request.symbol()) && e.getQuantity() < 0)
                .findFirst()
                .orElse(null);

        if (existingEntry != null) {
            int oldQty = Math.abs(existingEntry.getQuantity());
            int newQty = oldQty + request.quantity();
            double newAvgPrice = (oldQty * existingEntry.getShortEntryPrice() + request.quantity() * currentPrice) / newQty;
            existingEntry.setQuantity(-newQty);
            existingEntry.setShortEntryPrice(newAvgPrice);
            existingEntry.setMarginReserved(existingEntry.getMarginReserved() + marginRequired);
        } else {
            player.getPortfolio().add(PortfolioEntry.builder()
                    .symbol(request.symbol())
                    .quantity(-request.quantity())
                    .averageBuyPrice(0)
                    .shortEntryPrice(currentPrice)
                    .marginReserved(marginRequired)
                    .build());
        }

        playerRepository.save(player);

        Trade trade = Trade.builder()
                .playerId(playerId)
                .symbol(request.symbol())
                .type(TradeType.SHORT)
                .quantity(request.quantity())
                .price(currentPrice)
                .total(totalValue)
                .timestamp(Instant.now())
                .build();
        tradeRepository.save(trade);

        log.info("Short eröffnet: {} x {} @ {} für Spieler {}", request.quantity(), request.symbol(), currentPrice, playerId);

        return new TradeResponse(request.symbol(), TradeType.SHORT, request.quantity(), currentPrice, totalValue, Instant.now());
    }

    @Override
    public TradeResponse coverShort(String playerId, TradeRequest request) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Spieler nicht gefunden"));

        PortfolioEntry shortEntry = player.getPortfolio().stream()
                .filter(e -> e.getSymbol().equals(request.symbol()) && e.getQuantity() < 0)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Keine Short-Position für " + request.symbol()));

        int shortQty = Math.abs(shortEntry.getQuantity());
        if (request.quantity() > shortQty) {
            throw new IllegalArgumentException(
                    String.format("Nicht genug Short-Positionen. Haben: %d, Versucht: %d", shortQty, request.quantity()));
        }

        double currentPrice = getCurrentPrice(request.symbol());
        double coverCost = currentPrice * request.quantity();
        double originalValue = shortEntry.getShortEntryPrice() * request.quantity();
        double profitLoss = originalValue - coverCost; // Gewinn wenn Kurs gefallen

        // Margin anteilig freigeben
        double marginRelease = shortEntry.getMarginReserved() * ((double) request.quantity() / shortQty);
        player.setCash(player.getCash() + marginRelease + profitLoss);

        // Position aktualisieren oder entfernen
        if (request.quantity() == shortQty) {
            player.getPortfolio().remove(shortEntry);
        } else {
            shortEntry.setQuantity(-(shortQty - request.quantity()));
            shortEntry.setMarginReserved(shortEntry.getMarginReserved() - marginRelease);
        }

        playerRepository.save(player);

        Trade trade = Trade.builder()
                .playerId(playerId)
                .symbol(request.symbol())
                .type(TradeType.COVER)
                .quantity(request.quantity())
                .price(currentPrice)
                .total(coverCost)
                .timestamp(Instant.now())
                .build();
        tradeRepository.save(trade);

        log.info("Short gedeckt: {} x {} @ {} (P&L: {}) für Spieler {}",
                request.quantity(), request.symbol(), currentPrice, profitLoss, playerId);

        return new TradeResponse(request.symbol(), TradeType.COVER, request.quantity(), currentPrice, coverCost, Instant.now());
    }

    private double getCurrentPrice(String symbol) {
        List<StockPrice> prices = stockPriceRepository.findBySymbolOrderByTimestampDesc(symbol);
        if (prices.isEmpty()) {
            return stockRepository.findBySymbol(symbol)
                    .orElseThrow(() -> new IllegalArgumentException("Aktie nicht gefunden"))
                    .getInitialPrice();
        }
        return prices.get(0).getPrice();
    }
}
