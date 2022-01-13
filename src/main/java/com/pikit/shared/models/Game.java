package com.pikit.shared.models;

import lombok.*;

import java.util.HashMap;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    private HashMap<String, String> gameStats;
    private HashMap<String, String> homeTeamStats;
    private HashMap<String, String> awayTeamStats;
    private HashMap<String, String> bettingStats;
}
