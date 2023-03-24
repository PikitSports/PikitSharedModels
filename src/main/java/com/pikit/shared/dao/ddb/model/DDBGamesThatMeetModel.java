package com.pikit.shared.dao.ddb.model;

import com.pikit.shared.dao.ddb.converter.GamesThatMeetModelConverter;
import com.pikit.shared.models.GameThatMeetsModel;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.util.List;

@DynamoDbBean
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DDBGamesThatMeetModel {
    private static final String ID_ATTRIBUTE = "id";
    private static final String GAMES_ATTRIBUTE = "games";
    private static final String MODEL_ID_ATTRIBUTE = "modelId";
    private static final String SEASON_ATTRIBUTE = "season";
    private static final String MODEL_ID_INDEX = "modelIdIndex";

    @Getter(onMethod_ = {
            @DynamoDbAttribute(ID_ATTRIBUTE),
            @DynamoDbPartitionKey
    })
    private String id;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(MODEL_ID_ATTRIBUTE),
            @DynamoDbSecondaryPartitionKey(indexNames = MODEL_ID_INDEX)
    })
    private String modelId;

    @Getter(onMethod_ = {@DynamoDbAttribute(SEASON_ATTRIBUTE)})
    private String season;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(GAMES_ATTRIBUTE),
            @DynamoDbConvertedBy(GamesThatMeetModelConverter.class)
    })
    private List<GameThatMeetsModel> games;
}
