package com.wallstreetrookie.backend.model;

import com.wallstreetrookie.backend.model.enums.ChallengeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("challenges")
public class Challenge {

    @Id
    private String id;

    private String name;

    private String description;

    private ChallengeType type;

    private int durationWeeks;

    private double targetReturn;

    private String sectorRestriction;

    private boolean hasScheduledCrash;
}
