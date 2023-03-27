package com.pikit.shared.models;

import com.pikit.shared.enums.Bet;
import com.pikit.shared.enums.BetResult;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GameThatMeetsModel {
    private String gameId;
    private Map<Bet, BetResult> bets;
    private Map<String, String> homeTeamStats;
    private Map<String, String> awayTeamStats;
    private Map<String, String> gameStats;
    private Map<String, String> bettingStats;
}
