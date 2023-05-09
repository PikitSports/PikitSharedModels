package com.pikit.shared.dao.ddb.model;

import com.pikit.shared.dao.ddb.converter.MapAttributeConverter;
import com.pikit.shared.enums.League;
import com.pikit.shared.models.Game;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.util.Map;

@DynamoDbBean
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DDBGame {
    private static final String GAME_ID_ATTRIBUTE = "gameId";
    private static final String LEAGUE_ATTRIBUTE = "league";
    private static final String GAME_STATS_ATTRIBUTE = "gameStats";
    private static final String BETTING_STATS_ATTRIBUTE = "bettingStats";
    private static final String HOME_TEAM_STATS_ATTRIBUTE = "homeTeamStats";
    private static final String AWAY_TEAM_STATS_ATTRIBUTE = "awayTeamStats";

    @Getter(onMethod_ = {
            @DynamoDbAttribute(GAME_ID_ATTRIBUTE),
            @DynamoDbPartitionKey
    })
    private String gameId;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(LEAGUE_ATTRIBUTE),
            @DynamoDbSortKey
    })
    private League league;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(GAME_STATS_ATTRIBUTE),
            @DynamoDbConvertedBy(MapAttributeConverter.class)
    })
    private Map<String, String> gameStats;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(BETTING_STATS_ATTRIBUTE),
            @DynamoDbConvertedBy(MapAttributeConverter.class)
    })
    private Map<String, String> bettingStats;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(HOME_TEAM_STATS_ATTRIBUTE),
            @DynamoDbConvertedBy(MapAttributeConverter.class)
    })
    private Map<String, String> homeTeamStats;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(AWAY_TEAM_STATS_ATTRIBUTE),
            @DynamoDbConvertedBy(MapAttributeConverter.class)
    })
    private Map<String, String> awayTeamStats;

    public Game toGame() {
        return Game.builder()
                .gameStats(gameStats)
                .bettingStats(bettingStats)
                .homeTeamStats(homeTeamStats)
                .awayTeamStats(awayTeamStats)
                .build();
    }
}
