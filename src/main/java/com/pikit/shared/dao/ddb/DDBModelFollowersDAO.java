package com.pikit.shared.dao.ddb;

import com.pikit.shared.dao.ModelFollowersDAO;
import com.pikit.shared.dao.ddb.model.DDBModelFollowers;
import com.pikit.shared.exceptions.PersistenceException;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class DDBModelFollowersDAO implements ModelFollowersDAO {
    private final DynamoDbTable<DDBModelFollowers> modelFollowersTable;
    private final DynamoDbIndex<DDBModelFollowers> userIdIndex;

    public DDBModelFollowersDAO(DynamoDbTable<DDBModelFollowers> modelFollowersTable,
                                  DynamoDbIndex<DDBModelFollowers> userIdIndex) {
        this.modelFollowersTable = modelFollowersTable;
        this.userIdIndex = userIdIndex;
    }

    @Override
    public void followModel(String userId, String modelId) throws PersistenceException {
        try {
            DDBModelFollowers item = DDBModelFollowers.builder()
                    .modelId(modelId)
                    .userId(userId)
                    .dateFollowed(Instant.now().getEpochSecond())
                    .build();

            modelFollowersTable.putItem(PutItemEnhancedRequest.builder(DDBModelFollowers.class)
                    .item(item)
                    .build());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown following model for user {}:{}", userId, modelId, e);
            throw new PersistenceException("Failed to follow model for user");
        }
    }

    @Override
    public void unfollowModel(String userId, String modelId) throws PersistenceException {
        try {
            modelFollowersTable.deleteItem(DeleteItemEnhancedRequest.builder()
                    .key(Key.builder()
                            .partitionValue(modelId)
                            .sortValue(userId)
                            .build())
                    .build());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown unfollowing model for user {}:{}", userId, modelId, e);
            throw new PersistenceException("Failed to unfollowing model for user");
        }
    }

    @Override
    public List<String> getModelsUserFollows(String userId) throws PersistenceException {
        try {
            QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .partitionValue(userId)
                            .build()))
                    .build();

            return userIdIndex.query(request)
                    .stream()
                    .flatMap(page -> page.items().stream())
                    .map(DDBModelFollowers::getModelId)
                    .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown getting models user follows {}", userId, e);
            throw new PersistenceException("Failed to get models user follows");
        }
    }

    @Override
    public List<String> getFollowersForModel(String modelId) throws PersistenceException {
        try {
            QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .partitionValue(modelId)
                            .build()))
                    .build();

            return modelFollowersTable.query(request)
                    .stream()
                    .flatMap(page -> page.items().stream())
                    .map(DDBModelFollowers::getUserId)
                    .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown getting followers for model {}", modelId, e);
            throw new PersistenceException("Failed to get followers for model");
        }
    }
}
