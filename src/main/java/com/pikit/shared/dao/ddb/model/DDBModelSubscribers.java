package com.pikit.shared.dao.ddb.model;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DDBModelSubscribers {
    private static final String MODEL_ID_ATTRIBUTE = "modelId";
    private static final String USER_ID_ATTRIBUTE = "userId";
    private static final String DATE_SUBSCRIBED_ATTRIBUTE = "dateSubscribed";

    private static final String USER_ID_INDEX = "userIdIndex";

    @Getter(onMethod_ = {
            @DynamoDbAttribute(MODEL_ID_ATTRIBUTE),
            @DynamoDbPartitionKey
    })
    private String modelId;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(USER_ID_ATTRIBUTE),
            @DynamoDbSortKey,
            @DynamoDbSecondaryPartitionKey(indexNames = USER_ID_INDEX)
    })
    private String userId;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(DATE_SUBSCRIBED_ATTRIBUTE),
            @DynamoDbSecondarySortKey(indexNames = USER_ID_INDEX)
    })
    private Long dateSubscribed;
}
