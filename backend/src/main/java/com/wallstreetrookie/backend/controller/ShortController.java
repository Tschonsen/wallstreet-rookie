package com.wallstreetrookie.backend.controller;

import com.wallstreetrookie.backend.dto.request.TradeRequest;
import com.wallstreetrookie.backend.dto.response.TradeResponse;
import com.wallstreetrookie.backend.service.ShortSellingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trades")
@RequiredArgsConstructor
public class ShortController {

    private final ShortSellingService shortSellingService;

    @PostMapping("/short")
    public ResponseEntity<TradeResponse> openShort(
            Authentication auth,
            @Valid @RequestBody TradeRequest request) {
        return ResponseEntity.ok(shortSellingService.openShort(auth.getPrincipal().toString(), request));
    }

    @PostMapping("/cover")
    public ResponseEntity<TradeResponse> coverShort(
            Authentication auth,
            @Valid @RequestBody TradeRequest request) {
        return ResponseEntity.ok(shortSellingService.coverShort(auth.getPrincipal().toString(), request));
    }
}
