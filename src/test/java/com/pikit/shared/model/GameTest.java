package com.pikit.shared.model;

import com.pikit.shared.enums.League;
import com.pikit.shared.models.BettingStats;
import com.pikit.shared.models.Game;
import com.pikit.shared.models.GameStats;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GameTest {

    @Test
    public void sportsReferenceGameTest() {
        String homeTeam = "ATL";
        String awayTeam = "BAL";
        String gameDate = "01/02/2023";
        int numGameForDay = 0;

        Game game = Game.fromGameAndBettingStats(GameStats.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .gameDate(gameDate)
                .numGameForDay(numGameForDay)
                .build(), BettingStats.builder().build());

        assertThat(game.s3GameId(League.NFL)).isEqualTo("202301020atl");
        assertThat(game.s3GameId(League.NBA)).isEqualTo("202301020ATL");
        assertThat(game.s3GameId(League.MLB)).isEqualTo("ATL.ATL202301020");
    }

    @Test
    public void sportsReferenceTeamNameTest() {
        String homeTeam = "CHC";
        String awayTeam = "BAL";
        String gameDate = "01/02/2023";
        int numGameForDay = 0;

        Game game = Game.fromGameAndBettingStats(GameStats.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .gameDate(gameDate)
                .numGameForDay(numGameForDay)
                .build(), BettingStats.builder().build());

        assertThat(game.s3GameId(League.MLB)).isEqualTo("CHN.CHN202301020");
    }

    @Test
    public void lightWeightGameTest() {
        String wins = "wins";
        String losses = "losses";
        String ties = "ties";
        String homeTeam = "BAL";
        String awayTeam = "NYY";
        String gameDate = "01/02/2023";
        int numGameForDay = 0;

        String homeTeamWins = "1";
        String awayTeamWins = "2";
        String homeTeamLosses = "2";
        String awayTeamLosses = "1";
        String homeTeamTies = "6";
        String awayTeamTies = "3";

        String homeTeamMoneyLine = "200";
        String awayTeamMoneyLine = "-200";
        String homeTeamSpread = "2";
        String awayTeamSpread = "-2";
        String overUnder = "30";

        String stadiumType = "dome";
        String fieldType = "turf";

        Game game = Game.fromGameAndBettingStats(GameStats.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .gameDate(gameDate)
                .numGameForDay(numGameForDay)
                .build(), BettingStats.builder()
                .homeTeamMoneyLine(homeTeamMoneyLine)
                        .awayTeamMoneyLine(awayTeamMoneyLine)
                        .homeTeamSpread(homeTeamSpread)
                        .awayTeamSpread(awayTeamSpread)
                        .overUnder(overUnder)
                    .build());

        game.addHomeTeamStat(wins, homeTeamWins);
        game.addHomeTeamStat(losses, homeTeamLosses);
        game.addHomeTeamStat(ties, homeTeamTies);

        game.addAwayTeamStat(wins, awayTeamWins);
        game.addAwayTeamStat(losses, awayTeamLosses);
        game.addAwayTeamStat(ties, awayTeamTies);

        game.addGameStat("stadiumType", stadiumType);
        game.addGameStat("fieldType", fieldType);

        List<String> fieldsToReturn = List.of(wins, losses, "stadiumType");

        Game lightWeightGame = game.toLightWeightGame(fieldsToReturn);

        assertThat(lightWeightGame.homeTeam()).isNotNull();
        assertThat(lightWeightGame.awayTeam()).isNotNull();
        assertThat(lightWeightGame.gameDate()).isNotNull();
        assertThat(lightWeightGame.numGameForDay()).isNotNull();
        assertThat(lightWeightGame.homeTeamSpread()).isNotNull();
        assertThat(lightWeightGame.awayTeamSpread()).isNotNull();
        assertThat(lightWeightGame.homeTeamMoneyLine()).isNotNull();
        assertThat(lightWeightGame.awayTeamMoneyLine()).isNotNull();
        assertThat(lightWeightGame.overUnder()).isNotNull();

        //Assert that fields got filtered
        assertThat(lightWeightGame.getGameStats().get("fieldType")).isNull();
        assertThat(lightWeightGame.getHomeTeamStats().get(ties)).isNull();
        assertThat(lightWeightGame.getAwayTeamStats().get(ties)).isNull();

        //Assert that fields did not get filtered
        assertThat(lightWeightGame.getGameStats().get("stadiumType")).isEqualTo(stadiumType);
        assertThat(lightWeightGame.getHomeTeamStats().get(wins)).isEqualTo(homeTeamWins);
        assertThat(lightWeightGame.getAwayTeamStats().get(wins)).isEqualTo(awayTeamWins);
        assertThat(lightWeightGame.getHomeTeamStats().get(losses)).isEqualTo(homeTeamLosses);
        assertThat(lightWeightGame.getAwayTeamStats().get(losses)).isEqualTo(awayTeamLosses);
    }
}
