package com.wallstreetrookie.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameSettings {

    private double startingCash;

    private int totalWeeks;

    private boolean newsEnabled;
}