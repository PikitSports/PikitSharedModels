package com.pikit.shared.models.dao;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pikit.shared.dagger.DaggerServiceComponent;
import com.pikit.shared.dagger.ServiceComponent;
import com.pikit.shared.enums.League;
import com.pikit.shared.models.Game;
import com.pikit.shared.util.PikitSharedUtils;

/*
    NOT SURE IF WE SHOULD USE THIS CLASS OR NOT. COPIED IT FROM NON-USE CODE.
 */

public class GamesDAO {
    private static ServiceComponent serviceComponent = DaggerServiceComponent.create();
    private static Table nflTable;
    private static Table nbaTable;
    private static PikitSharedUtils pikitSharedUtils;

    public GamesDAO() { this(serviceComponent); }

    public GamesDAO(ServiceComponent serviceComponent) {
        nflTable = serviceComponent.getNflGamesTable();
        nbaTable = serviceComponent.getNbaGamesTable();
        pikitSharedUtils = serviceComponent.getSharedUtil();
    }

    public void uploadGameToDynamo(Game gameData, League league) {
        String gameDate = gameData.getGameStats().get("gameDate");
        String key = pikitSharedUtils.getGameIdFromGame(gameData);

        String season = getSeasonFromGameDate(gameDate);
        Item item = new Item().withPrimaryKey("gameIdentifier", key)
                .withMap("gameStats", gameData.getGameStats())
                .withMap("homeTeamStats", gameData.getHomeTeamStats())
                .withMap("awayTeamStats", gameData.getAwayTeamStats())
                .withMap("bettingStats", gameData.getBettingStats())
                .withString("gameStatus", "UPCOMING")
                .withString("season", season);

        Table table = getTableFromLeague(league);
        table.putItem(item);
    }

    private Table getTableFromLeague(League league) {
        if (league.equals(League.NFL)) {
            return nflTable;
        } else if (league.equals(League.NBA)) {
            return nbaTable;
        } else {
            throw new RuntimeException("Invalid League: " + league);
        }
    }

    private String getSeasonFromGameDate(String gameDate) {
        String[] dateSplit = gameDate.split("/");
        return dateSplit[2];
    }
}
