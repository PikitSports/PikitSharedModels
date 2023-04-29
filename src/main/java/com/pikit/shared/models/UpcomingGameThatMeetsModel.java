package com.pikit.shared.models;

import lombok.*;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpcomingGameThatMeetsModel {
    private String gameId;
    private Map<String, String> gameStats;
    private Map<String, String> bettingStats;
    private Map<String, String> homeTeamStats;
    private Map<String, String> awayTeamStats;
    private List<UpcomingBetToTake> betsToTake;
}
