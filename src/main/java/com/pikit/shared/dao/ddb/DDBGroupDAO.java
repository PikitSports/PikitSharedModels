package com.pikit.shared.dao.ddb;

import com.pikit.shared.dao.GroupDAO;
import com.pikit.shared.dao.ddb.model.DDBGroup;
import com.pikit.shared.exceptions.NotFoundException;
import com.pikit.shared.exceptions.PersistenceException;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
public class DDBGroupDAO implements GroupDAO {

    private final DynamoDbTable<DDBGroup> groupsTable;
    private final DynamoDbIndex<DDBGroup> userGroupsIndex;

    public DDBGroupDAO(DynamoDbTable<DDBGroup> groupsTable, DynamoDbIndex<DDBGroup> userGroupsIndex) {
        this.groupsTable = groupsTable;
        this.userGroupsIndex = userGroupsIndex;
    }

    @Override
    public String createGroup(String userId, String modelId, String groupName) throws PersistenceException {
        String groupId = UUID.randomUUID().toString().replace("-", "");
        long creationTimestamp = System.currentTimeMillis();

        DDBGroup groupToSave = DDBGroup.builder()
                .groupId(groupId)
                .groupName(groupName)
                .modelIds(Collections.singletonList(modelId))
                .userCreatedBy(userId)
                .creationTimestamp(creationTimestamp)
                .lastUpdatedTimestamp(creationTimestamp)
                .build();

        try {
            groupsTable.putItem(groupToSave);
            return groupId;
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown creating group for {}", userId, e);
            throw new PersistenceException("Failed to save group");
        }
    }

    @Override
    public void addModelToGroup(String groupId, String modelId) throws PersistenceException, NotFoundException {
        try {
            Optional<DDBGroup> groupOptional = getGroupFromId(groupId);

            if (!groupOptional.isPresent()) {
                log.error("Group attempting to update was not found {}", groupId);
                throw new NotFoundException("Group not found");
            }

            DDBGroup groupToUpdate = groupOptional.get();
            List<String> modelIds = groupToUpdate.getModelIds();
            modelIds.add(modelId);
            groupToUpdate.setModelIds(modelIds);
            groupToUpdate.setLastUpdatedTimestamp(System.currentTimeMillis());

            UpdateItemEnhancedRequest<DDBGroup> request = UpdateItemEnhancedRequest.builder(DDBGroup.class)
                    .item(groupToUpdate)
                    .ignoreNulls(true)
                    .build();

            groupsTable.updateItem(request);
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown updating group {}", groupId, e);
            throw new PersistenceException("Failed to update group");
        }
    }

    @Override
    public Optional<DDBGroup> getGroupFromId(String groupId) throws PersistenceException {
        try {
            GetItemEnhancedRequest request = GetItemEnhancedRequest.builder()
                    .key(Key.builder()
                            .partitionValue(groupId)
                            .build())
                    .consistentRead(true)
                    .build();

            DDBGroup group = groupsTable.getItem(request);

            if (group != null) {
                return Optional.of(group);
            } else {
                return Optional.empty();
            }
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown retrieving group {}", groupId, e);
            throw new PersistenceException("Failed to get group");
        }
    }

    @Override
    public List<DDBGroup> getGroupsForUser(String userId) throws PersistenceException {
        try {
            QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .partitionValue(userId)
                            .build()))
                    .build();

            return userGroupsIndex.query(request)
                    .stream()
                    .flatMap(page -> page.items().stream())
                    .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown retrieving groups for user {}", userId, e);
            throw new PersistenceException("Failed to get groups for user");
        }
    }

    @Override
    public void deleteGroup(String groupId) throws PersistenceException {
        try {
            DeleteItemEnhancedRequest request = DeleteItemEnhancedRequest.builder()
                    .key(Key.builder()
                            .partitionValue(groupId)
                            .build())
                    .build();
            groupsTable.deleteItem(request);
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown deleting group {}", groupId, e);
            throw new PersistenceException("Failed to delete group");
        }
    }
}
