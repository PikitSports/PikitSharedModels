package com.pikit.shared.dao.ddb.model;

import com.pikit.shared.dao.ddb.converter.ModelConfigurationConverter;
import com.pikit.shared.dao.ddb.converter.ModelPerformanceConverter;
import com.pikit.shared.enums.League;
import com.pikit.shared.models.ModelConfiguration;
import com.pikit.shared.models.ModelPerformance;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DDBModel {
    private static final String MODEL_ID_ATTRIBUTE = "modelId";
    private static final String USER_CREATED_BY_ATTRIBUTE = "userCreatedBy";
    private static final String MODEL_CONFIGURATION_ATTRIBUTE = "modelConfiguration";
    private static final String CREATION_TIMESTAMP_ATTRIBUTE = "creationTimestamp";
    private static final String LAST_UPDATED_TIMESTAMP_ATTRIBUTE = "lastUpdatedTimestamp";
    private static final String LEAGUE_ATTRIBUTE = "league";
    private static final String MODEL_PERFORMANCE_ATTRIBUTE = "modelPerformance";
    private static final String TOP_3_GAMES_ATTRIBUTE = "top3Games";
    private static final String MODEL_STATUS_ATTRIBUTE = "modelStatus";
    private static final String MODEL_WORKFLOW_EXECUTION_ATTRIBUTE = "modelWorkflowExecution";
    private static final String LAST_10_GAMES_ATTRIBUTE = "last10Games";
    private static final String LAST_50_GAMES_ATTRIBUTE = "last50Games";
    private static final String LAST_100_GAMES_ATTRIBUTE = "last100Games";

    //Indexes
    private static final String LEAGUE_INDEX = "leagueIndex";
    private static final String LAST_10_GAMES_INDEX = "last10GamesIndex";
    private static final String LAST_50_GAMES_INDEX = "last50GamesIndex";
    private static final String LAST_100_GAMES_INDEX = "last100GamesIndex";

    private static final String USER_MODELS_INDEX = "userModelsIndex";

    @Getter(onMethod_ = { @DynamoDbAttribute(MODEL_ID_ATTRIBUTE), @DynamoDbPartitionKey})
    private String modelId;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(USER_CREATED_BY_ATTRIBUTE),
            @DynamoDbSecondaryPartitionKey(indexNames = USER_MODELS_INDEX)})
    private String userCreatedBy;

    private League league;

    private Long creationTimestamp;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(MODEL_CONFIGURATION_ATTRIBUTE),
            @DynamoDbConvertedBy(ModelConfigurationConverter.class)})
    private ModelConfiguration modelConfiguration;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(MODEL_PERFORMANCE_ATTRIBUTE),
            @DynamoDbConvertedBy(ModelPerformanceConverter.class)})
    private ModelPerformance modelPerformance;

    @Getter(onMethod_ = {@DynamoDbAttribute(LAST_UPDATED_TIMESTAMP_ATTRIBUTE)})
    private Long lastUpdatedTimestamp;

    @Getter(onMethod_ = {@DynamoDbAttribute(MODEL_STATUS_ATTRIBUTE)})
    private ModelStatus modelStatus;

    @Getter(onMethod_ = {@DynamoDbAttribute(MODEL_WORKFLOW_EXECUTION_ATTRIBUTE)})
    private String modelWorkflowExecution;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(LAST_10_GAMES_ATTRIBUTE),
            @DynamoDbSecondarySortKey(indexNames = LAST_10_GAMES_INDEX)
    })
    private Double last10Games;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(LAST_50_GAMES_ATTRIBUTE),
            @DynamoDbSecondarySortKey(indexNames = LAST_50_GAMES_INDEX)
    })
    private Double last50Games;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(LAST_100_GAMES_ATTRIBUTE),
            @DynamoDbSecondarySortKey(indexNames = LAST_100_GAMES_INDEX)
    })
    private Double last100Games;

    @DynamoDbAttribute(LEAGUE_ATTRIBUTE)
    @DynamoDbSecondaryPartitionKey(indexNames = {LEAGUE_INDEX, LAST_10_GAMES_INDEX, LAST_50_GAMES_INDEX, LAST_100_GAMES_INDEX})
    public League getLeague() {
        return league;
    }

    @DynamoDbAttribute(CREATION_TIMESTAMP_ATTRIBUTE)
    @DynamoDbSecondarySortKey(indexNames = {USER_MODELS_INDEX, LEAGUE_INDEX})
    public Long getCreationTimestamp() {
        return creationTimestamp;
    }
}
