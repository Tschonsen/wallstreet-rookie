package com.wallstreetrookie.backend.service.impl;

import com.wallstreetrookie.backend.dto.response.LeaderboardEntry;
import com.wallstreetrookie.backend.model.Player;
import com.wallstreetrookie.backend.repository.PlayerRepository;
import com.wallstreetrookie.backend.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private final PlayerRepository playerRepository;

    @Override
    public List<LeaderboardEntry> getLeaderboard() {
        AtomicInteger rank = new AtomicInteger(1);

        return playerRepository.findAll().stream()
                .sorted(Comparator.comparingDouble(Player::getTotalValue).reversed())
                .limit(50)
                .map(player -> new LeaderboardEntry(
                        rank.getAndIncrement(),
                        player.getUsername(),
                        player.getTotalValue()
                ))
                .toList();
    }
}
