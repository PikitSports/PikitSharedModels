package com.pikit.shared.models;

import com.pikit.shared.enums.Bet;
import com.pikit.shared.enums.BetResult;
import lombok.*;

import java.util.HashMap;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GameThatMeetsModel {
    private String gameId;
    private HashMap<Bet, BetResult> bets;
    private HashMap<String, String> homeTeamStats;
    private HashMap<String, String> awayTeamStats;
    private HashMap<String, String> gameStats;
    private HashMap<String, String> bettingStats;
}
