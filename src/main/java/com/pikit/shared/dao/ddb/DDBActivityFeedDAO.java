package com.pikit.shared.dao.ddb;

import com.pikit.shared.dao.ActivityFeedDAO;
import com.pikit.shared.dao.ddb.model.DDBActivity;
import com.pikit.shared.dao.ddb.model.DDBGame;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.activity.ActivityData;
import com.pikit.shared.models.activity.ActivityFeed;
import com.pikit.shared.models.activity.ActivityType;
import com.pikit.shared.models.activity.LastActivitySeen;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
public class DDBActivityFeedDAO implements ActivityFeedDAO {
    private static final int EXPIRATION_DAYS = 7;
    private final DynamoDbTable<DDBActivity> activityFeedTable;
    private final DynamoDbIndex<DDBActivity> userFeedIndex;

    public DDBActivityFeedDAO(DynamoDbTable<DDBActivity> activityFeedTable,
                              DynamoDbIndex<DDBActivity> userFeedIndex) {
        this.activityFeedTable = activityFeedTable;
        this.userFeedIndex = userFeedIndex;
    }

    @Override
    public void saveActivity(String activityId, String user, ActivityType activityType, ActivityData activityData) throws PersistenceException {
        long activityTimestamp = Instant.now().getEpochSecond();
        long expirationTimestamp =  Instant.now().plus(EXPIRATION_DAYS, ChronoUnit.DAYS).getEpochSecond();

        try {
            DDBActivity activity = DDBActivity.builder()
                    .activityId(activityId)
                    .user(user)
                    .activityTimestamp(activityTimestamp)
                    .activityType(activityType)
                    .activityData(activityData)
                    .expirationTimestamp(expirationTimestamp)
                    .build();

            activityFeedTable.putItem(PutItemEnhancedRequest.builder(DDBActivity.class)
                    .item(activity)
                    .build());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown saving activity {} for user {}", activityId, user, e);
            throw new PersistenceException("Failed to save activity for user");
        }
    }

    @Override
    public ActivityFeed getActivityFeed(String user, int pageSize, Optional<LastActivitySeen> lastActivitySeenOptional) throws PersistenceException {
        try {
            Map<String, AttributeValue> exclusiveStartKey = null;

            if (lastActivitySeenOptional.isPresent()) {
                exclusiveStartKey.put("activityId", AttributeValue.builder().s(lastActivitySeenOptional.get().getActivityId()).build());
                exclusiveStartKey.put("activityTimestamp", AttributeValue.builder().n(lastActivitySeenOptional.get().getActivityTimestamp().toString()).build());
                exclusiveStartKey.put("user", AttributeValue.builder().s(lastActivitySeenOptional.get().getUser()).build());
            }


            QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .partitionValue(user)
                            .build()))
                    .limit(pageSize)
                    .exclusiveStartKey(exclusiveStartKey)
                    .build();

            List<DDBActivity> activityList = userFeedIndex.query(request)
                    .stream()
                    .flatMap(page -> page.items().stream())
                    .collect(Collectors.toList());

            DDBActivity lastActivitySeen = activityList.get(activityList.size() - 1);

            return ActivityFeed.builder()
                    .activityList(activityList)
                    .lastActivitySeen(LastActivitySeen.builder()
                            .activityId(lastActivitySeen.getActivityId())
                            .activityTimestamp(lastActivitySeen.getActivityTimestamp())
                            .user(lastActivitySeen.getUser())
                            .build())
                    .build();
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown retrieving activity feed for user {}", user, e);
            throw new PersistenceException("Failed to get activity feed for user");
        }
    }
}
