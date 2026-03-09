package com.wallstreetrookie.backend.service.impl;

import com.wallstreetrookie.backend.dto.response.StockResponse;
import com.wallstreetrookie.backend.exception.StockNotFoundException;
import com.wallstreetrookie.backend.mapper.StockMapper;
import com.wallstreetrookie.backend.model.StockModel;
import com.wallstreetrookie.backend.model.StockPrice;
import com.wallstreetrookie.backend.repository.StockPriceRepository;
import com.wallstreetrookie.backend.repository.StockRepository;
import com.wallstreetrookie.backend.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final StockPriceRepository stockPriceRepository;
    private final StockMapper stockMapper;

    @Override
    public List<StockResponse> getAllStocks() {
        return stockRepository.findAll().stream()
                .map(stock -> {
                    StockPrice latestPrice = getLatestPrice(stock.getSymbol());
                    return stockMapper.toResponse(stock, latestPrice);
                })
                .toList();
    }

    @Override
    public StockResponse getStock(String symbol) {
        StockModel stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new StockNotFoundException("Aktie '" + symbol + "' nicht gefunden"));
        StockPrice latestPrice = getLatestPrice(symbol);
        return stockMapper.toResponse(stock, latestPrice);
    }

    private StockPrice getLatestPrice(String symbol) {
        List<StockPrice> prices = stockPriceRepository.findBySymbolOrderByTimestampDesc(symbol);
        if (prices.isEmpty()) {
            return StockPrice.builder()
                    .symbol(symbol)
                    .price(0.0)
                    .change(0.0)
                    .changePercent(0.0)
                    .build();
        }
        return prices.getFirst();
    }
}
