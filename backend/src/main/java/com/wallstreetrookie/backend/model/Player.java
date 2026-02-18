package com.wallstreetrookie.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("players")
public class Player {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private String passwordHash;

    @Indexed(unique = true)
    private String email;

    private double cash;

    private double totalValue;

    private List<PortfolioEntry> portfolio;

    private Instant createdAt;
}