package com.wallstreetrookie.backend.service.impl;

import com.wallstreetrookie.backend.model.DividendPayment;
import com.wallstreetrookie.backend.model.Player;
import com.wallstreetrookie.backend.model.StockModel;
import com.wallstreetrookie.backend.model.StockPrice;
import com.wallstreetrookie.backend.repository.DividendPaymentRepository;
import com.wallstreetrookie.backend.repository.PlayerRepository;
import com.wallstreetrookie.backend.repository.StockPriceRepository;
import com.wallstreetrookie.backend.repository.StockRepository;
import com.wallstreetrookie.backend.service.DividendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DividendServiceImpl implements DividendService {

    private final PlayerRepository playerRepository;
    private final StockRepository stockRepository;
    private final StockPriceRepository stockPriceRepository;
    private final DividendPaymentRepository dividendPaymentRepository;

    @Override
    public int payDividends(String gameSessionId, List<String> playerIds) {
        // Alle Aktien mit Dividende laden
        Map<String, Double> dividendYields = stockRepository.findAll().stream()
                .filter(s -> s.getDividendYield() > 0)
                .collect(Collectors.toMap(StockModel::getSymbol, StockModel::getDividendYield));

        if (dividendYields.isEmpty()) return 0;

        int totalPayments = 0;

        for (String playerId : playerIds) {
            Player player = playerRepository.findById(playerId).orElse(null);
            if (player == null || player.getPortfolio() == null) continue;

            double totalDividend = 0;

            for (var entry : player.getPortfolio()) {
                if (entry.getQuantity() <= 0) continue; // Keine Dividende für Short-Positionen

                Double yield = dividendYields.get(entry.getSymbol());
                if (yield == null || yield <= 0) continue;

                double currentPrice = getCurrentPrice(entry.getSymbol());
                // Monatliche Dividende = Kurs × Jahresrendite / 12
                double dividend = currentPrice * yield / 100.0 / 12.0 * entry.getQuantity();

                dividendPaymentRepository.save(DividendPayment.builder()
                        .playerId(playerId)
                        .symbol(entry.getSymbol())
                        .amount(dividend)
                        .timestamp(Instant.now())
                        .gameSessionId(gameSessionId)
                        .build());

                totalDividend += dividend;
                totalPayments++;
            }

            if (totalDividend > 0) {
                player.setCash(player.getCash() + totalDividend);
                playerRepository.save(player);
                log.info("Dividende ausgezahlt: ${} an Spieler {}", String.format("%.2f", totalDividend), playerId);
            }
        }

        return totalPayments;
    }

    private double getCurrentPrice(String symbol) {
        List<StockPrice> prices = stockPriceRepository.findBySymbolOrderByTimestampDesc(symbol);
        if (prices.isEmpty()) {
            return stockRepository.findBySymbol(symbol)
                    .map(StockModel::getInitialPrice)
                    .orElse(0.0);
        }
        return prices.get(0).getPrice();
    }
}
