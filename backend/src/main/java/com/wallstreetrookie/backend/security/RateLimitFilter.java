package com.wallstreetrookie.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int GENERAL_LIMIT = 60;       // 60 requests per minute
    private static final int TRADE_LIMIT = 10;          // 10 trades per minute
    private static final long WINDOW_MS = 60_000;       // 1 minute window

    private final Map<String, Queue<Long>> generalRequests = new ConcurrentHashMap<>();
    private final Map<String, Queue<Long>> tradeRequests = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String userId = request.getUserPrincipal() != null
                ? request.getUserPrincipal().getName()
                : request.getRemoteAddr();

        // General rate limit
        if (isRateLimited(userId, generalRequests, GENERAL_LIMIT)) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Zu viele Anfragen. Bitte warte einen Moment.\"}");
            return;
        }

        // Trade-specific rate limit
        String path = request.getRequestURI();
        if (path.startsWith("/trades") && "POST".equalsIgnoreCase(request.getMethod())) {
            if (isRateLimited(userId, tradeRequests, TRADE_LIMIT)) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Maximale Trade-Anzahl erreicht. Max 10 Trades pro Minute.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRateLimited(String key, Map<String, Queue<Long>> store, int limit) {
        long now = Instant.now().toEpochMilli();
        Queue<Long> timestamps = store.computeIfAbsent(key, k -> new ConcurrentLinkedQueue<>());

        // Remove expired entries
        while (!timestamps.isEmpty() && now - timestamps.peek() > WINDOW_MS) {
            timestamps.poll();
        }

        if (timestamps.size() >= limit) {
            return true;
        }

        timestamps.add(now);
        return false;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/auth/")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/api-docs")
                || path.startsWith("/ws");
    }
}
