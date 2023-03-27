package com.pikit.shared.models;

import com.sun.tools.javac.util.List;
import lombok.*;

import java.util.HashMap;

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

    private HashMap<String, String> gameStats;
    private HashMap<String, String> homeTeamStats;
    private HashMap<String, String> awayTeamStats;
    private HashMap<String, String> bettingStats;

    public String getGameId() {
        String homeTeam = gameStats.get(HOME_TEAM);
        String awayTeam = gameStats.get(AWAY_TEAM);
        String gameDate = gameStats.get(GAME_DATE);
        String numGameForDay = gameStats.get(NUM_GAME_FOR_DAY);

        return String.format("%s|%s|%s|%s", homeTeam, awayTeam, gameDate, numGameForDay);
    }

    public String getGameWinner() {
        return gameStats.get(GAME_WINNER);
    }

    public String getHomeTeam() {
        return gameStats.get(HOME_TEAM);
    }

    public String getAwayTeam() {
        return gameStats.get(AWAY_TEAM);
    }

    public String getTeamThatCoveredTheSpread() {
        return gameStats.get(TEAM_THAT_COVERED_THE_SPREAD);
    }

    public Double getHomeTeamMoneyLine() {
        return getSanitizedDouble(bettingStats.get(HOME_TEAM_MONEY_LINE));
    }

    public Double getAwayTeamMoneyLine() {
        return getSanitizedDouble(bettingStats.get(AWAY_TEAM_MONEY_LINE));
    }

    public Double getHomeTeamSpread() {
        return getSanitizedDouble(bettingStats.get(HOME_TEAM_SPREAD));
    }

    public Double getAwayTeamSpread() {
        return getSanitizedDouble(bettingStats.get(AWAY_TEAM_SPREAD));
    }

    public Double getOverUnder() {
        return getSanitizedDouble(bettingStats.get(OVER_UNDER));
    }

    public Double getHomeTeamScore() {
        return getSanitizedDouble(homeTeamStats.get(FINAL));
    }

    public Double getAwayTeamScore() {
        return getSanitizedDouble(awayTeamStats.get(FINAL));
    }

    private Double getSanitizedDouble(String value) {
        if (List.of("pk", "PK", "x", "-", "NL", "").contains(value)) {
            return 0.0;
        }

        return Double.parseDouble(value);
    }
}
