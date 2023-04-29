package com.pikit.shared.models;

import com.pikit.shared.datasource.SportsReferenceConstants;
import com.pikit.shared.enums.League;
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

    private static final String SPORTS_REFERENCE_MLB_GAME_FORMAT= "%s.%s%s%s";
    private static final String SPORTS_REFERENCE_NBA_GAME_FORMAT= "%s%s%s";
    private static final String SPORTS_REFERENCE_NFL_GAME_FORMAT= "%s%s%s";

    //Fields to serialize
    private Map<String, String> gameStats;
    private Map<String, String> homeTeamStats;
    private Map<String, String> awayTeamStats;
    private Map<String, String> bettingStats;

    public String gameId() {
        String homeTeam = gameStats.get(HOME_TEAM);
        String awayTeam = gameStats.get(AWAY_TEAM);
        String gameDate = gameStats.get(GAME_DATE);
        String numGameForDay = gameStats.getOrDefault(NUM_GAME_FOR_DAY, "0");

        return String.format("%s|%s|%s|%s", homeTeam, awayTeam, gameDate, numGameForDay);
    }

    public String s3GameId(League league) {
        String sportsReferenceTeamName = SportsReferenceConstants.getSportsReferenceTeamName(league, homeTeam());
        switch(league) {
            case MLB:
                return String.format(SPORTS_REFERENCE_MLB_GAME_FORMAT, sportsReferenceTeamName, sportsReferenceTeamName, sportsReferenceGameDate(), numGameForDay());
            case NBA:
                return String.format(SPORTS_REFERENCE_NBA_GAME_FORMAT, sportsReferenceGameDate(), numGameForDay(), sportsReferenceTeamName);
            case NFL:
                return String.format(SPORTS_REFERENCE_NFL_GAME_FORMAT, sportsReferenceGameDate(), numGameForDay(), sportsReferenceTeamName.toLowerCase());
            default:
                throw new RuntimeException("Invalid league");
        }
    }

    //Our game date: 01/02/2023
    //SR game date: 20230102
    public String sportsReferenceGameDate() {
        String[] dates = gameDate().split("/");
        return dates[2] + dates[0] + dates[1];
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

    public String gameDate() { return gameStats.get(GAME_DATE); }

    public String numGameForDay() { return gameStats.get(NUM_GAME_FOR_DAY); }

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

    public static Game fromGameAndBettingStats(GameStats gameStatsToMerge, BettingStats bettingStatsToMerge) {
        Map<String, String> gameStats = new HashMap<>();
        Map<String, String> bettingStats = new HashMap<>();

        gameStats.put(HOME_TEAM, gameStatsToMerge.getHomeTeam());
        gameStats.put(AWAY_TEAM, gameStatsToMerge.getAwayTeam());
        gameStats.put(GAME_DATE, gameStatsToMerge.getGameDate());
        gameStats.put(NUM_GAME_FOR_DAY, String.valueOf(gameStatsToMerge.getNumGameForDay()));

        bettingStats.put(HOME_TEAM_MONEY_LINE, bettingStatsToMerge.getHomeTeamMoneyLine());
        bettingStats.put(HOME_TEAM_SPREAD, bettingStatsToMerge.getHomeTeamSpread());
        bettingStats.put(AWAY_TEAM_MONEY_LINE, bettingStatsToMerge.getAwayTeamMoneyLine());
        bettingStats.put(AWAY_TEAM_SPREAD, bettingStatsToMerge.getAwayTeamSpread());
        bettingStats.put(OVER_UNDER, bettingStatsToMerge.getOverUnder());

        return Game.builder()
                .gameStats(gameStats)
                .bettingStats(bettingStats)
                .homeTeamStats(new HashMap<>())
                .awayTeamStats(new HashMap<>())
                .build();
    }

    public GameStats toGameStats() {
        return GameStats.builder()
                .homeTeam(homeTeam())
                .awayTeam(awayTeam())
                .gameDate(gameDate())
                .numGameForDay(Integer.parseInt(numGameForDay()))
                .build();
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
