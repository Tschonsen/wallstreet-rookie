package com.wallstreetrookie.backend.config;

import com.wallstreetrookie.backend.model.Challenge;
import com.wallstreetrookie.backend.model.enums.ChallengeType;
import com.wallstreetrookie.backend.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class ChallengeDataLoader implements CommandLineRunner {

    private final ChallengeRepository challengeRepository;

    @Override
    public void run(String... args) {
        if (challengeRepository.count() > 0) {
            return;
        }

        List<Challenge> challenges = List.of(
                Challenge.builder()
                        .name("Freies Spiel")
                        .description("Kein Zeitlimit, kein Ziel. Experimentiere frei mit dem Markt.")
                        .type(ChallengeType.FREE_PLAY)
                        .durationWeeks(0)
                        .targetReturn(0)
                        .hasScheduledCrash(false)
                        .build(),
                Challenge.builder()
                        .name("Jahres-Challenge")
                        .description("Erziele die höchste Rendite in 52 Wochen (1 Börsenjahr).")
                        .type(ChallengeType.YEARLY)
                        .durationWeeks(52)
                        .targetReturn(20)
                        .hasScheduledCrash(false)
                        .build(),
                Challenge.builder()
                        .name("Tech-Meister")
                        .description("Handel nur mit Technologie-Aktien. Ziel: 30% Rendite in 26 Wochen.")
                        .type(ChallengeType.SECTOR)
                        .durationWeeks(26)
                        .targetReturn(30)
                        .sectorRestriction("Technologie")
                        .hasScheduledCrash(false)
                        .build(),
                Challenge.builder()
                        .name("Energie-Experte")
                        .description("Handel nur mit Energie-Aktien. Ziel: 30% Rendite in 26 Wochen.")
                        .type(ChallengeType.SECTOR)
                        .durationWeeks(26)
                        .targetReturn(30)
                        .sectorRestriction("Energie")
                        .hasScheduledCrash(false)
                        .build(),
                Challenge.builder()
                        .name("Crash-Survivor")
                        .description("Ein Markt-Crash kommt in Woche 4-8. Überlebe und ende im Plus!")
                        .type(ChallengeType.CRASH_SURVIVOR)
                        .durationWeeks(52)
                        .targetReturn(0)
                        .hasScheduledCrash(true)
                        .build()
        );

        challengeRepository.saveAll(challenges);
        log.info("{} Challenges geladen", challenges.size());
    }
}
