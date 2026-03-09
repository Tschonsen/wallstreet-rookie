package com.wallstreetrookie.backend.engine;

import com.wallstreetrookie.backend.model.News;
import com.wallstreetrookie.backend.model.StockModel;
import com.wallstreetrookie.backend.model.StockPrice;
import com.wallstreetrookie.backend.model.enums.MarketEventType;
import com.wallstreetrookie.backend.repository.NewsRepository;
import com.wallstreetrookie.backend.repository.StockPriceRepository;
import com.wallstreetrookie.backend.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketEventService {

    private final StockRepository stockRepository;
    private final StockPriceRepository stockPriceRepository;
    private final NewsRepository newsRepository;
    private final Random random = new Random();

    private static final String[] SECTORS = {
            "Technologie", "Finanzen", "Gesundheit", "Energie", "Konsum", "Industrie", "Krypto/FinTech"
    };

    private static final int[] EVENT_WEIGHTS = {
            5,   // MARKET_CRASH
            5,   // BULL_RUN
            15,  // SECTOR_BOOM
            15,  // SECTOR_CRISIS
            10,  // INTEREST_RATE_HIKE
            20,  // SCANDAL
            20,  // TAKEOVER_RUMOR
            10   // IPO_HYPE
    };

    public boolean shouldTriggerEvent(int tickCount, boolean isMultiplayer) {
        if (isMultiplayer) {
            // MP: ca. alle 10-20 Minuten (20-40 Ticks bei 30s/Tick)
            return tickCount > 0 && tickCount % (20 + random.nextInt(21)) == 0;
        } else {
            // SP: ca. alle 4-8 Wochen
            return random.nextInt(8) < 1;
        }
    }

    public List<News> triggerRandomEvent(String gameSessionId) {
        MarketEventType eventType = selectWeightedEvent();
        log.info("Markt-Event ausgelöst: {} für Session {}", eventType, gameSessionId);
        return applyEvent(eventType, gameSessionId);
    }

    private MarketEventType selectWeightedEvent() {
        int totalWeight = 0;
        for (int w : EVENT_WEIGHTS) totalWeight += w;

        int roll = random.nextInt(totalWeight);
        int cumulative = 0;
        MarketEventType[] types = MarketEventType.values();

        for (int i = 0; i < types.length; i++) {
            cumulative += EVENT_WEIGHTS[i];
            if (roll < cumulative) {
                return types[i];
            }
        }
        return types[types.length - 1];
    }

    private List<News> applyEvent(MarketEventType eventType, String gameSessionId) {
        return switch (eventType) {
            case MARKET_CRASH -> applyMarketCrash(gameSessionId);
            case BULL_RUN -> applyBullRun(gameSessionId);
            case SECTOR_BOOM -> applySectorBoom(gameSessionId);
            case SECTOR_CRISIS -> applySectorCrisis(gameSessionId);
            case INTEREST_RATE_HIKE -> applyInterestRateHike(gameSessionId);
            case SCANDAL -> applyScandal(gameSessionId);
            case TAKEOVER_RUMOR -> applyTakeoverRumor(gameSessionId);
            case IPO_HYPE -> applyIpoHype(gameSessionId);
        };
    }

    private List<News> applyMarketCrash(String gameSessionId) {
        double factor = -(0.10 + random.nextDouble() * 0.20); // -10% bis -30%
        List<StockModel> stocks = stockRepository.findAll();
        applyPriceChange(stocks, factor, gameSessionId);

        News news = News.builder()
                .title("BÖRSEN-CRASH: Panikverkäufe an allen Märkten!")
                .content("Ein massiver Ausverkauf erschüttert die globalen Finanzmärkte. Anleger flüchten in sichere Häfen.")
                .affectedSymbols(stocks.stream().map(StockModel::getSymbol).toList())
                .affectedSector("Alle")
                .impact(factor)
                .timestamp(Instant.now())
                .gameSessionId(gameSessionId)
                .build();

        return List.of(newsRepository.save(news));
    }

    private List<News> applyBullRun(String gameSessionId) {
        double factor = 0.10 + random.nextDouble() * 0.15; // +10% bis +25%
        List<StockModel> stocks = stockRepository.findAll();
        applyPriceChange(stocks, factor, gameSessionId);

        News news = News.builder()
                .title("BULL-RUN: Märkte im Höhenrausch!")
                .content("Optimismus treibt die Kurse auf breiter Front nach oben. Rekordjagd an den Börsen.")
                .affectedSymbols(stocks.stream().map(StockModel::getSymbol).toList())
                .affectedSector("Alle")
                .impact(factor)
                .timestamp(Instant.now())
                .gameSessionId(gameSessionId)
                .build();

        return List.of(newsRepository.save(news));
    }

    private List<News> applySectorBoom(String gameSessionId) {
        String sector = SECTORS[random.nextInt(SECTORS.length)];
        double factor = 0.15 + random.nextDouble() * 0.15; // +15% bis +30%
        List<StockModel> sectorStocks = stockRepository.findBySector(sector);
        applyPriceChange(sectorStocks, factor, gameSessionId);

        News news = News.builder()
                .title(sector + "-Boom: Branche erlebt explosives Wachstum!")
                .content("Der " + sector + "-Sektor profitiert von bahnbrechenden Entwicklungen. Analysten erhöhen Kursziele.")
                .affectedSymbols(sectorStocks.stream().map(StockModel::getSymbol).toList())
                .affectedSector(sector)
                .impact(factor)
                .timestamp(Instant.now())
                .gameSessionId(gameSessionId)
                .build();

        return List.of(newsRepository.save(news));
    }

    private List<News> applySectorCrisis(String gameSessionId) {
        String sector = SECTORS[random.nextInt(SECTORS.length)];
        double factor = -(0.15 + random.nextDouble() * 0.10); // -15% bis -25%
        List<StockModel> sectorStocks = stockRepository.findBySector(sector);
        applyPriceChange(sectorStocks, factor, gameSessionId);

        News news = News.builder()
                .title(sector + "-Krise: Branche unter massivem Druck!")
                .content("Regulatorische Bedenken und schwache Zahlen belasten den " + sector + "-Sektor schwer.")
                .affectedSymbols(sectorStocks.stream().map(StockModel::getSymbol).toList())
                .affectedSector(sector)
                .impact(factor)
                .timestamp(Instant.now())
                .gameSessionId(gameSessionId)
                .build();

        return List.of(newsRepository.save(news));
    }

    private List<News> applyInterestRateHike(String gameSessionId) {
        List<News> newsList = new ArrayList<>();

        // Finanzen profitiert
        List<StockModel> financeStocks = stockRepository.findBySector("Finanzen");
        double finFactor = 0.08 + random.nextDouble() * 0.12; // +8% bis +20%
        applyPriceChange(financeStocks, finFactor, gameSessionId);

        newsList.add(newsRepository.save(News.builder()
                .title("Zinserhöhung: Finanzsektor jubelt!")
                .content("Die Zentralbank erhöht den Leitzins. Banken und Versicherungen profitieren von höheren Margen.")
                .affectedSymbols(financeStocks.stream().map(StockModel::getSymbol).toList())
                .affectedSector("Finanzen")
                .impact(finFactor)
                .timestamp(Instant.now())
                .gameSessionId(gameSessionId)
                .build()));

        // Tech leidet
        List<StockModel> techStocks = stockRepository.findBySector("Technologie");
        double techFactor = -(0.08 + random.nextDouble() * 0.12); // -8% bis -20%
        applyPriceChange(techStocks, techFactor, gameSessionId);

        newsList.add(newsRepository.save(News.builder()
                .title("Zinserhöhung belastet Tech-Werte!")
                .content("Höhere Zinsen machen Wachstumsaktien weniger attraktiv. Tech-Sektor unter Druck.")
                .affectedSymbols(techStocks.stream().map(StockModel::getSymbol).toList())
                .affectedSector("Technologie")
                .impact(techFactor)
                .timestamp(Instant.now())
                .gameSessionId(gameSessionId)
                .build()));

        return newsList;
    }

    private List<News> applyScandal(String gameSessionId) {
        List<StockModel> allStocks = stockRepository.findAll();
        StockModel target = allStocks.get(random.nextInt(allStocks.size()));
        double factor = -(0.20 + random.nextDouble() * 0.20); // -20% bis -40%
        applyPriceChange(List.of(target), factor, gameSessionId);

        News news = News.builder()
                .title("SKANDAL bei " + target.getName() + "!")
                .content("Schwere Vorwürfe gegen " + target.getName() + ": Ermittlungen wegen Bilanzmanipulation eingeleitet. Aktie bricht ein.")
                .affectedSymbols(List.of(target.getSymbol()))
                .affectedSector(target.getSector())
                .impact(factor)
                .timestamp(Instant.now())
                .gameSessionId(gameSessionId)
                .build();

        return List.of(newsRepository.save(news));
    }

    private List<News> applyTakeoverRumor(String gameSessionId) {
        List<StockModel> allStocks = stockRepository.findAll();
        StockModel target = allStocks.get(random.nextInt(allStocks.size()));
        double factor = 0.15 + random.nextDouble() * 0.20; // +15% bis +35%
        applyPriceChange(List.of(target), factor, gameSessionId);

        News news = News.builder()
                .title("Übernahme-Gerücht: " + target.getName() + " im Visier!")
                .content("Ein Branchenriese soll Interesse an einer Übernahme von " + target.getName() + " haben. Die Aktie schießt nach oben.")
                .affectedSymbols(List.of(target.getSymbol()))
                .affectedSector(target.getSector())
                .impact(factor)
                .timestamp(Instant.now())
                .gameSessionId(gameSessionId)
                .build();

        return List.of(newsRepository.save(news));
    }

    private List<News> applyIpoHype(String gameSessionId) {
        String sector = SECTORS[random.nextInt(SECTORS.length)];
        double factor = 0.10 + random.nextDouble() * 0.15; // +10% bis +25%
        List<StockModel> sectorStocks = stockRepository.findBySector(sector);
        applyPriceChange(sectorStocks, factor, gameSessionId);

        News news = News.builder()
                .title("IPO-Hype im " + sector + "-Sektor!")
                .content("Ein spektakulärer Börsengang befeuert das Interesse am gesamten " + sector + "-Sektor. Anleger strömen in die Branche.")
                .affectedSymbols(sectorStocks.stream().map(StockModel::getSymbol).toList())
                .affectedSector(sector)
                .impact(factor)
                .timestamp(Instant.now())
                .gameSessionId(gameSessionId)
                .build();

        return List.of(newsRepository.save(news));
    }

    private void applyPriceChange(List<StockModel> stocks, double factor, String gameSessionId) {
        for (StockModel stock : stocks) {
            List<StockPrice> prices = stockPriceRepository.findBySymbolOrderByTimestampDesc(stock.getSymbol());
            double lastPrice = prices.isEmpty() ? stock.getInitialPrice() : prices.get(0).getPrice();
            double newPrice = Math.max(0.01, lastPrice * (1 + factor));
            double change = newPrice - lastPrice;
            double changePercent = (change / lastPrice) * 100;

            stockPriceRepository.save(StockPrice.builder()
                    .symbol(stock.getSymbol())
                    .price(newPrice)
                    .change(change)
                    .changePercent(changePercent)
                    .timestamp(Instant.now())
                    .gameSessionId(gameSessionId)
                    .build());
        }
    }
}
