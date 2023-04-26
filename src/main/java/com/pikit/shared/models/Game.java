package com.pikit.shared.models;

import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Game {
    private static final String HOME_TEAM = "homeTeam";
    private static final String AWAY_TEAM = "awayTeam";
    private static final String GAME_DATE = "gameDate";
    private static final String NUM_GAME_FOR_DAY = "numGameForDay";
    private static final String TEAM_THAT_COVERED_THE_SPREAD = "teamThatCoveredTheSpread";
    private static final String GAME_WINNER = "gameWinner";
    private static final String HOME_TEAM_MONEY_LINE = "homeTeamMoneyLine";
    private static final String AWAY_TEAM_MONEY_LINE = "awayTeamMoneyLine";
    private static final String HOME_TEAM_SPREAD = "homeTeamSpread";
    private static final String AWAY_TEAM_SPREAD = "awayTeamSpread";
    private static final String FINAL = "Final";
    private static final String OVER_UNDER = "overUnder";

    //Fields to serialize
    private HashMap<String, String> gameStats;
    private HashMap<String, String> homeTeamStats;
    private HashMap<String, String> awayTeamStats;
    private HashMap<String, String> bettingStats;

    public String gameId() {
        String homeTeam = gameStats.get(HOME_TEAM);
        String awayTeam = gameStats.get(AWAY_TEAM);
        String gameDate = gameStats.get(GAME_DATE);
        String numGameForDay = gameStats.getOrDefault(NUM_GAME_FOR_DAY, "1");

        return String.format("%s|%s|%s|%s", homeTeam, awayTeam, gameDate, numGameForDay);
    }

    public String gameWinner() {
        return gameStats.get(GAME_WINNER);
    }

    public String homeTeam() {
        return gameStats.get(HOME_TEAM);
    }

    public String awayTeam() {
        return gameStats.get(AWAY_TEAM);
    }

    public String teamThatCoveredTheSpread() {
        return gameStats.get(TEAM_THAT_COVERED_THE_SPREAD);
    }

    public Double homeTeamMoneyLine() {
        return getSanitizedDouble(bettingStats.get(HOME_TEAM_MONEY_LINE));
    }

    public Double awayTeamMoneyLine() {
        return getSanitizedDouble(bettingStats.get(AWAY_TEAM_MONEY_LINE));
    }

    public Double homeTeamSpread() {
        return getSanitizedDouble(bettingStats.get(HOME_TEAM_SPREAD));
    }

    public Double awayTeamSpread() {
        return getSanitizedDouble(bettingStats.get(AWAY_TEAM_SPREAD));
    }

    public Double overUnder() {
        return getSanitizedDouble(bettingStats.get(OVER_UNDER));
    }

    public Double homeTeamScore() {
        return getSanitizedDouble(homeTeamStats.get(FINAL));
    }

    public Double awayTeamScore() {
        return getSanitizedDouble(awayTeamStats.get(FINAL));
    }

    public void mergeGame(Game gameToMerge) {
        gameStats.putAll(gameToMerge.getGameStats());
        bettingStats.putAll(gameToMerge.getBettingStats());
        homeTeamStats.putAll(gameToMerge.getHomeTeamStats());
        awayTeamStats.putAll(gameToMerge.getAwayTeamStats());
    }

    private Double getSanitizedDouble(String value) {
        if (value == null) {
            return null;
        }

        if (List.of("pk", "PK", "x", "-", "NL", "").contains(value)) {
            return 0.0;
        }

        return Double.parseDouble(value);
    }
}
