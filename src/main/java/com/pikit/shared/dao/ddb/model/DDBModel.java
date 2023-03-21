package com.pikit.shared.dao.ddb.model;

import com.pikit.shared.dao.ddb.converter.ModelConfigurationConverter;
import com.pikit.shared.dao.ddb.converter.ModelPerformanceConverter;
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

    private static final String USER_MODELS_INDEX = "userModelsIndex";

    @Getter(onMethod_ = { @DynamoDbAttribute(MODEL_ID_ATTRIBUTE), @DynamoDbPartitionKey})
    private String modelId;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(USER_CREATED_BY_ATTRIBUTE),
            @DynamoDbSecondaryPartitionKey(indexNames = USER_MODELS_INDEX)})
    private String userCreatedBy;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(CREATION_TIMESTAMP_ATTRIBUTE),
            @DynamoDbSecondarySortKey(indexNames = USER_MODELS_INDEX)})
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
}
