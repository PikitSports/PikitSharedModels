package com.pikit.shared.models;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatsForUpcomingGame {
    private String gameId;
    private Game game;
    private Integer numHomeTeamMoneyLine;
    private Integer numHomeTeamSpread;
    private Integer numAwayTeamMoneyLine;
    private Integer numAwayTeamSpread;
    private Integer numOver;
    private Integer numUnder;
}
