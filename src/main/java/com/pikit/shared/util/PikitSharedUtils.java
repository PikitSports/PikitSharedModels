package com.pikit.shared.util;

import com.pikit.shared.models.Game;

public class PikitSharedUtils {
    public static String getGameIdFromGame(Game game) {
        String homeTeam = game.getGameStats().get("homeTeam");
        String awayTeam = game.getGameStats().get("awayTeam");
        String gameDate = game.getGameStats().get("gameDate");

        return homeTeam + "|" + awayTeam + "|" + gameDate;
    }
}
