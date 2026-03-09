package com.wallstreetrookie.backend.controller;

import com.wallstreetrookie.backend.dto.response.AchievementResponse;
import com.wallstreetrookie.backend.dto.response.ChallengeResponse;
import com.wallstreetrookie.backend.model.Challenge;
import com.wallstreetrookie.backend.repository.ChallengeRepository;
import com.wallstreetrookie.backend.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeRepository challengeRepository;
    private final AchievementService achievementService;

    @GetMapping
    public ResponseEntity<List<ChallengeResponse>> getChallenges() {
        List<ChallengeResponse> challenges = challengeRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(challenges);
    }

    @GetMapping("/achievements")
    public ResponseEntity<List<AchievementResponse>> getAchievements(Authentication auth) {
        return ResponseEntity.ok(
                achievementService.getPlayerAchievements(auth.getPrincipal().toString()));
    }

    private ChallengeResponse toResponse(Challenge c) {
        return new ChallengeResponse(
                c.getId(), c.getName(), c.getDescription(), c.getType(),
                c.getDurationWeeks(), c.getTargetReturn(), c.getSectorRestriction());
    }
}
