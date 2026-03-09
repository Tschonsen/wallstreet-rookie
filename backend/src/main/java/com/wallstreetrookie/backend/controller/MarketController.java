package com.wallstreetrookie.backend.controller;

import com.wallstreetrookie.backend.dto.response.NewsResponse;
import com.wallstreetrookie.backend.dto.response.StockResponse;
import com.wallstreetrookie.backend.mapper.NewsMapper;
import com.wallstreetrookie.backend.model.StockPrice;
import com.wallstreetrookie.backend.repository.NewsRepository;
import com.wallstreetrookie.backend.repository.StockPriceRepository;
import com.wallstreetrookie.backend.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/market")
@RequiredArgsConstructor
public class MarketController {

    private final StockService stockService;
    private final StockPriceRepository stockPriceRepository;
    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;

    @GetMapping("/stocks")
    public ResponseEntity<List<StockResponse>> getAllStocks() {
        return ResponseEntity.ok(stockService.getAllStocks());
    }

    @GetMapping("/stocks/{symbol}")
    public ResponseEntity<StockResponse> getStock(@PathVariable String symbol) {
        return ResponseEntity.ok(stockService.getStock(symbol));
    }

    @GetMapping("/stocks/{symbol}/history")
    public ResponseEntity<List<StockPrice>> getStockHistory(@PathVariable String symbol) {
        return ResponseEntity.ok(stockPriceRepository.findBySymbolOrderByTimestampDesc(symbol));
    }

    @GetMapping("/news")
    public ResponseEntity<List<NewsResponse>> getNews(@RequestParam(required = false) String gameSessionId) {
        if (gameSessionId != null) {
            return ResponseEntity.ok(newsRepository.findByGameSessionId(gameSessionId).stream()
                    .map(newsMapper::toResponse)
                    .toList());
        }
        return ResponseEntity.ok(newsRepository.findAll().stream()
                .map(newsMapper::toResponse)
                .toList());
    }
}
