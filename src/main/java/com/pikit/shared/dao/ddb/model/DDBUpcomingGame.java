package com.pikit.shared.dao.ddb.model;

import com.pikit.shared.dao.ddb.converter.UpcomingGameThatMeetsModelConverter;
import com.pikit.shared.models.UpcomingGameThatMeetsModel;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DDBUpcomingGame {
    private static final String GAME_ID_ATTRIBUTE = "gameId";
    private static final String MODEL_ID_ATTRIBUTE = "modelId";
    private static final String GAME_ATTRIBUTE = "game";

    private static final String MODEL_ID_INDEX = "modelIdIndex";

    @Getter(onMethod_ = {
            @DynamoDbAttribute(GAME_ID_ATTRIBUTE),
            @DynamoDbPartitionKey
    })
    private String gameId;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(MODEL_ID_ATTRIBUTE),
            @DynamoDbSortKey,
            @DynamoDbSecondaryPartitionKey(indexNames = MODEL_ID_INDEX)
    })
    private String modelId;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(GAME_ATTRIBUTE),
            @DynamoDbConvertedBy(UpcomingGameThatMeetsModelConverter.class)
    })
    private UpcomingGameThatMeetsModel upcomingGame;
}
