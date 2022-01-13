package com.pikit.shared.models;

import lombok.*;

import java.util.HashMap;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpcomingGameThatMeetsModel {
    private String gameId;
    private String modelId;
    private HashMap<String, String> gameStats;
    private HashMap<String, String> bettingStats;
    private HashMap<String, String> homeTeamStats;
    private HashMap<String, String> awayTeamStats;
    private List<UpcomingBetToTake> betsToTake;
}
