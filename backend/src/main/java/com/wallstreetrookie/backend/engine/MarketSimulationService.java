package com.wallstreetrookie.backend.engine;

import com.wallstreetrookie.backend.model.News;
import com.wallstreetrookie.backend.model.StockModel;
import com.wallstreetrookie.backend.model.StockPrice;
import com.wallstreetrookie.backend.repository.NewsRepository;
import com.wallstreetrookie.backend.repository.StockPriceRepository;
import com.wallstreetrookie.backend.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MarketSimulationService {

    private static final double MIN_PRICE = 0.01;

    private static final Map<String, Double> SECTOR_TRENDS = Map.of(
            "Technologie", 0.01,
            "Finanzen", 0.005,
            "Gesundheit", 0.008,
            "Energie", -0.005,
            "Konsum", 0.003,
            "Industrie", 0.002,
            "Krypto/FinTech", 0.015
    );

    private final StockRepository stockRepository;
    private final StockPriceRepository stockPriceRepository;
    private final NewsRepository newsRepository;
    private final Random random = new Random();

    public void simulateTick(String gameSessionId) {
        List<StockModel> stocks = stockRepository.findAll();

        for (StockModel stock : stocks) {
            double lastPrice = getLastPrice(stock);
            double newPrice = calculateNewPrice(stock, lastPrice, gameSessionId);

            double change = newPrice - lastPrice;
            double changePercent = (change / lastPrice) * 100;

            StockPrice stockPrice = StockPrice.builder()
                    .symbol(stock.getSymbol())
                    .price(newPrice)
                    .change(change)
                    .changePercent(changePercent)
                    .timestamp(Instant.now())
                    .gameSessionId(gameSessionId)
                    .build();

            stockPriceRepository.save(stockPrice);
        }
    }

    private double calculateNewPrice(StockModel stock, double lastPrice, String gameSessionId) {
        double trend = SECTOR_TRENDS.getOrDefault(stock.getSector(), 0.0);
        double zufall = random.nextGaussian() * stock.getVolatility();
        double newsImpact = getNewsImpact(stock.getSymbol(), stock.getSector(), gameSessionId);

        double newPrice = lastPrice * (1 + trend + zufall + newsImpact);

        return Math.max(newPrice, MIN_PRICE);
    }

    private double getLastPrice(StockModel stock) {
        List<StockPrice> prices = stockPriceRepository.findBySymbolOrderByTimestampDesc(stock.getSymbol());
        if (prices.isEmpty()) {
            return stock.getInitialPrice();
        }
        return prices.getFirst().getPrice();
    }

    private double getNewsImpact(String symbol, String sector, String gameSessionId) {
        if (gameSessionId == null) {
            return 0.0;
        }

        List<News> newsList = newsRepository.findByGameSessionId(gameSessionId);
        double totalImpact = 0.0;

        for (News news : newsList) {
            if (news.getAffectedSymbols() != null && news.getAffectedSymbols().contains(symbol)) {
                totalImpact += news.getImpact();
            } else if (news.getAffectedSector() != null && news.getAffectedSector().equals(sector)) {
                totalImpact += news.getImpact() * 0.5;
            }
        }

        return Math.max(-0.1, Math.min(0.1, totalImpact));
    }
}
