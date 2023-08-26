package com.pikit.shared.dao.ddb;

import com.pikit.shared.dao.ModelSubscribersDAO;
import com.pikit.shared.dao.ddb.model.DDBModelSubscribers;
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
public class DDBModelSubscribersDAO implements ModelSubscribersDAO {
    private final DynamoDbTable<DDBModelSubscribers> modelSubscribersTable;
    private final DynamoDbIndex<DDBModelSubscribers> userIdIndex;

    public DDBModelSubscribersDAO(DynamoDbTable<DDBModelSubscribers> modelSubscribersTable,
                                  DynamoDbIndex<DDBModelSubscribers> userIdIndex) {
        this.modelSubscribersTable = modelSubscribersTable;
        this.userIdIndex = userIdIndex;
    }

    @Override
    public void subscribeUserToModel(String userId, String modelId) throws PersistenceException {
        try {
            DDBModelSubscribers item = DDBModelSubscribers.builder()
                    .modelId(modelId)
                    .userId(userId)
                    .dateSubscribed(Instant.now().getEpochSecond())
                    .build();

            modelSubscribersTable.putItem(PutItemEnhancedRequest.builder(DDBModelSubscribers.class)
                    .item(item)
                    .build());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown subscribing user to model {}:{}", userId, modelId, e);
            throw new PersistenceException("Failed to subscribe user to model");
        }
    }

    @Override
    public void unsubscribeUserFromModel(String userId, String modelId) throws PersistenceException {
        try {
            modelSubscribersTable.deleteItem(DeleteItemEnhancedRequest.builder()
                            .key(Key.builder()
                                    .partitionValue(modelId)
                                    .sortValue(userId)
                                    .build())
                            .build());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown unsubscribing user from model {}:{}", userId, modelId, e);
            throw new PersistenceException("Failed to unsubscribe user from model");
        }
    }

    @Override
    public List<String> getModelsUserSubscribesTo(String userId) throws PersistenceException {
        try {
            QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                                    .partitionValue(userId)
                                    .build()))
                    .build();

            return userIdIndex.query(request)
                    .stream()
                    .flatMap(page -> page.items().stream())
                    .map(DDBModelSubscribers::getModelId)
                    .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown getting models user subscribes to {}", userId, e);
            throw new PersistenceException("Failed to get models user subscribes to");
        }
    }

    @Override
    public List<String> getSubscribersForModel(String modelId) throws PersistenceException {
        try {
            QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .partitionValue(modelId)
                            .build()))
                    .build();

            return modelSubscribersTable.query(request)
                    .stream()
                    .flatMap(page -> page.items().stream())
                    .map(DDBModelSubscribers::getUserId)
                    .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown getting users subscribed to model {}", modelId, e);
            throw new PersistenceException("Failed to get users subscribed to model");
        }
    }
}
