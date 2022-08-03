package com.pikit.shared.util;

import com.pikit.shared.models.Game;

public class PikitSharedUtils {
    public String getGameIdFromGame(Game game) {
        String homeTeam = game.getGameStats().get("homeTeam");
        String awayTeam = game.getGameStats().get("awayTeam");
        String gameDate = game.getGameStats().get("gameDate");

        String gameId = homeTeam + "|" + awayTeam + "|" + gameDate;

        if (game.getGameStats().containsKey("numGameForDay")) {
            gameId += "|" + game.getGameStats().get("numGameForDay");
        }

        return gameId;
    }
}
