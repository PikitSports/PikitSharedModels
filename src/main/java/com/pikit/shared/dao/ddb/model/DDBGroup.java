package com.pikit.shared.dao.ddb.model;

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
public class DDBGroup {

    private static final String GROUP_ID_ATTRIBUTE = "groupId";
    private static final String GROUP_NAME_ATTRIBUTE = "groupName";
    private static final String USER_CREATED_BY_ATTRIBUTE = "userCreatedBy";
    private static final String MODEL_IDS_ATTRIBUTE = "modelIds";
    private static final String CREATION_TIMESTAMP_ATTRIBUTE = "creationTimestamp";
    private static final String LAST_UPDATED_TIMESTAMP_ATTRIBUTE = "lastUpdatedTimestamp";

    // Indexes
    private static final String USER_GROUPS_INDEX = "userGroupsIndex";

    @Getter(onMethod_ = {
            @DynamoDbAttribute(GROUP_ID_ATTRIBUTE),
            @DynamoDbPartitionKey
    })
    private String groupId;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(GROUP_NAME_ATTRIBUTE)
    })
    private String groupName;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(USER_CREATED_BY_ATTRIBUTE),
            @DynamoDbSecondaryPartitionKey(indexNames = USER_GROUPS_INDEX)
    })
    private String userCreatedBy;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(MODEL_IDS_ATTRIBUTE)
    })
    private List<String> modelIds;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(CREATION_TIMESTAMP_ATTRIBUTE)
    })
    private Long creationTimestamp;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(LAST_UPDATED_TIMESTAMP_ATTRIBUTE),
            @DynamoDbSecondarySortKey(indexNames = USER_GROUPS_INDEX)
    })
    private Long lastUpdatedTimestamp;

}
