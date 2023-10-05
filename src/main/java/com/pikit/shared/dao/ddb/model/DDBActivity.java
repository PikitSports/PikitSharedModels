package com.pikit.shared.dao.ddb.model;

import com.pikit.shared.dao.ddb.converter.ActivityDataConverter;
import com.pikit.shared.models.activity.ActivityData;
import com.pikit.shared.models.activity.ActivityType;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DDBActivity {
    private static final String ACTIVITY_ID_ATTRIBUTE = "activityId";
    private static final String USER_ATTRIBUTE = "user";
    private static final String ACTIVITY_TIMESTAMP_ATTRIBUTE = "activityTimestamp";
    private static final String ACTIVITY_TYPE_ATTRIBUTE = "activityType";
    private static final String ACTIVITY_DATA_ATTRIBUTE = "activityData";
    private static final String EXPIRATION_TIMESTAMP_ATTRIBUTE = "expirationTimestamp";
    private static final String USER_FEED_INDEX = "userFeedIndex";

    @Getter(onMethod_ = {
            @DynamoDbAttribute(ACTIVITY_ID_ATTRIBUTE),
            @DynamoDbPartitionKey
    })
    private String activityId;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(USER_ATTRIBUTE),
            @DynamoDbSortKey,
            @DynamoDbSecondaryPartitionKey(indexNames = USER_FEED_INDEX)
    })
    private String user;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(ACTIVITY_TIMESTAMP_ATTRIBUTE),
            @DynamoDbSecondarySortKey(indexNames = USER_FEED_INDEX)
    })
    private Long activityTimestamp;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(ACTIVITY_TYPE_ATTRIBUTE)
    })
    private ActivityType activityType;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(ACTIVITY_DATA_ATTRIBUTE),
            @DynamoDbConvertedBy(ActivityDataConverter.class)
    })
    private ActivityData activityData;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(EXPIRATION_TIMESTAMP_ATTRIBUTE)
    })
    private Long expirationTimestamp;
}
