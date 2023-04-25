package com.pikit.shared.datasource;

import com.pikit.shared.enums.League;

public final class DataSourceConstants {
    private DataSourceConstants() {}

    public static final String NFL_CURRENT_SEASON = "2023";
    public static final String MLB_CURRENT_SEASON = "2023";
    public static final String NBA_CURRENT_SEASON = "2022";

    public static String getCurrentSeasonForLeague(League league) {
        switch (league) {
            case NFL: return NFL_CURRENT_SEASON;
            case MLB: return MLB_CURRENT_SEASON;
            case NBA: return NBA_CURRENT_SEASON;
            default: throw new RuntimeException("Invalid league: " + league);
        }
    }
}
