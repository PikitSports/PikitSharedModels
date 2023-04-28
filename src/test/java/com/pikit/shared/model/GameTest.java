package com.pikit.shared.model;

import com.pikit.shared.enums.League;
import com.pikit.shared.models.BettingStats;
import com.pikit.shared.models.Game;
import com.pikit.shared.models.GameStats;
import org.junit.jupiter.api.Test;

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
}
