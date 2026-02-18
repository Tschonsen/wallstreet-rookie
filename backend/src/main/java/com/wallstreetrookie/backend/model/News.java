package com.wallstreetrookie.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("news")
public class News {

    @Id
    private String id;

    private String title;

    private String content;

    private List<String> affectedSymbols;

    private String affectedSector;

    private double impact;

    private Instant timestamp;

    private String gameSessionId;
}