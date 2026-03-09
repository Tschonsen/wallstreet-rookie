package com.wallstreetrookie.backend.config;

import com.wallstreetrookie.backend.model.StockModel;
import com.wallstreetrookie.backend.model.StockPrice;
import com.wallstreetrookie.backend.repository.StockPriceRepository;
import com.wallstreetrookie.backend.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final StockRepository stockRepository;
    private final StockPriceRepository stockPriceRepository;

    @Override
    public void run(String... args) {
        if (stockRepository.count() > 0) {
            log.info("Seed Data bereits vorhanden, überspringe Import.");
            return;
        }

        log.info("Lade Seed Data: 25 Aktien...");

        List<StockModel> stocks = List.of(
                // Technologie (Volatilität: 0.03)
                stock("TNOV", "TechNova", "Technologie", 150.00, 0.03),
                stock("CBYT", "CloudByte", "Technologie", 85.00, 0.03),
                stock("PXFG", "PixelForge", "Technologie", 220.00, 0.03),
                stock("DSTR", "DataStream", "Technologie", 95.00, 0.03),

                // Finanzen (Volatilität: 0.02)
                stock("GBNK", "GlobalBank", "Finanzen", 65.00, 0.02),
                stock("TCAP", "TrustCapital", "Finanzen", 120.00, 0.02),
                stock("CEDG", "CoinEdge", "Finanzen", 45.00, 0.02),
                stock("SVLT", "SafeVault", "Finanzen", 78.00, 0.02),

                // Gesundheit (Volatilität: 0.015)
                stock("MCUR", "MediCure", "Gesundheit", 180.00, 0.015),
                stock("BGNX", "BioGenix", "Gesundheit", 95.00, 0.015),
                stock("HLTH", "HealthFirst", "Gesundheit", 140.00, 0.015),
                stock("PHRM", "PharmaPeak", "Gesundheit", 110.00, 0.015),

                // Energie (Volatilität: 0.025)
                stock("SLPK", "SolarPeak", "Energie", 70.00, 0.025),
                stock("WNDF", "WindForce", "Energie", 55.00, 0.025),
                stock("OILG", "OilGiant", "Energie", 90.00, 0.025),
                stock("GRGD", "GreenGrid", "Energie", 42.00, 0.025),

                // Konsum (Volatilität: 0.01)
                stock("FMRT", "FreshMart", "Konsum", 35.00, 0.01),
                stock("LUXB", "LuxBrand", "Konsum", 250.00, 0.01),
                stock("QKBT", "QuickBite", "Konsum", 28.00, 0.01),
                stock("STLH", "StyleHub", "Konsum", 60.00, 0.01),

                // Industrie (Volatilität: 0.02)
                stock("STLW", "SteelWorks", "Industrie", 48.00, 0.02),
                stock("ADRV", "AutoDrive", "Industrie", 175.00, 0.02),
                stock("BLDC", "BuildCore", "Industrie", 82.00, 0.02),
                stock("ARJT", "AeroJet", "Industrie", 310.00, 0.02),

                // Krypto/FinTech (Volatilität: 0.05)
                stock("BKTD", "BlockTrade", "Krypto/FinTech", 420.00, 0.05)
        );

        List<StockModel> savedStocks = stockRepository.saveAll(stocks);

        List<StockPrice> initialPrices = savedStocks.stream()
                .map(s -> StockPrice.builder()
                        .symbol(s.getSymbol())
                        .price(s.getInitialPrice())
                        .change(0.0)
                        .changePercent(0.0)
                        .timestamp(Instant.now())
                        .build())
                .toList();

        stockPriceRepository.saveAll(initialPrices);

        log.info("Seed Data geladen: {} Aktien mit Startkursen.", savedStocks.size());
    }

    private StockModel stock(String symbol, String name, String sector, double price, double volatility) {
        return StockModel.builder()
                .symbol(symbol)
                .name(name)
                .sector(sector)
                .initialPrice(price)
                .volatility(volatility)
                .build();
    }
}
