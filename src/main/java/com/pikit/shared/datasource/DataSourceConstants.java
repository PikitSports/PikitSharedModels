package com.pikit.shared.datasource;

import com.pikit.shared.enums.League;
import com.pikit.shared.enums.ModelTimeRange;

import java.util.List;

public final class DataSourceConstants {
    private DataSourceConstants() {}

    private static final String DATASOURCE_CONFIG_PATH = "%s/dataSourceConfig.json";
    private static final String STAT_CONFIGURATION_PATH = "%s/statConfiguration.json";
    private static final String STANDINGS_PATH = "%s/standings/%s/%s.json";
    private static final String TEAM_METADATA_PATH = "%s/teamMetadata/%s.json";

    private static final String NFL_CURRENT_SEASON = "2023";
    private static final String MLB_CURRENT_SEASON = "2023";
    private static final String NBA_CURRENT_SEASON = "2023";

    private static final List<String> NFL_MODEL_SEASONS = List.of("2023", "2022", "2021", "2020");
    private static final List<String> MLB_MODEL_SEASONS = List.of("2023", "2022", "2021");
    private static final List<String> NBA_MODEL_SEASONS = List.of("2023", "2022", "2021", "2020");

    public static String getCurrentSeasonForLeague(League league) {
        switch (league) {
            case NFL: return NFL_CURRENT_SEASON;
            case MLB: return MLB_CURRENT_SEASON;
            case NBA: return NBA_CURRENT_SEASON;
            default: throw new RuntimeException("Invalid league: " + league);
        }
    }

    public static String getDataSourcePathForLeague(League league) {
        return String.format(DATASOURCE_CONFIG_PATH, league);
    }

    public static String getStatConfigurationPathForLeague(League league) {
        return String.format(STAT_CONFIGURATION_PATH, league);
    }

    public static String getStandingsPathForLeague(League league, String statName, String season) {
        return String.format(STANDINGS_PATH, league, statName, season);
    }

    public static String getTeamMetadataPathForLeague(League league, String season) {
        return String.format(TEAM_METADATA_PATH, league, season);
    }

    public static List<String> getModelSeasons(League league, ModelTimeRange timeRange) {
        //Currently don't care about time range => All models will use the same time range. This will be updated later.
        switch (league) {
            case NFL: return NFL_MODEL_SEASONS;
            case MLB: return MLB_MODEL_SEASONS;
            case NBA: return NBA_MODEL_SEASONS;
            default: throw new RuntimeException("Invalid league: " + league);
        }
    }
}
