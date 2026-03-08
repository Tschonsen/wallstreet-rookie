package com.wallstreetrookie.backend.service;

import com.wallstreetrookie.backend.dto.response.LeaderboardEntry;

import java.util.List;

public interface LeaderboardService {

    List<LeaderboardEntry> getLeaderboard();
}
