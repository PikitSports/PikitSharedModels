package com.pikit.shared.dao.ddb;

import com.pikit.shared.dao.UserDAO;
import com.pikit.shared.dao.ddb.model.DDBUser;
import com.pikit.shared.exceptions.PersistenceException;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
public class DDBUserDAO implements UserDAO {
    private final DynamoDbTable<DDBUser> usersTable;

    public DDBUserDAO(DynamoDbTable<DDBUser> usersTable) {
        this.usersTable = usersTable;
    }

    @Override
    public void createUser(DDBUser user) throws PersistenceException {
        try {
            usersTable.putItem(user);
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown creating user {}", user.getUserId(), e);
            throw new PersistenceException("Failed to create user");
        }
    }

    @Override
    public void updateUser(DDBUser user) throws PersistenceException {
        try {
            usersTable.updateItem(UpdateItemEnhancedRequest.builder(DDBUser.class)
                    .item(user)
                    .ignoreNulls(true)
                    .build());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown updating user {}", user.getUserId(), e);
            throw new PersistenceException("Failed to update user");
        }
    }

    @Override
    public Optional<DDBUser> getUser(String userId) throws PersistenceException {
        try {
            DDBUser user = usersTable.getItem(Key.builder()
                    .partitionValue(userId)
                    .build());

            if (user != null) {
                return Optional.of(user);
            } else {
                return Optional.empty();
            }
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown getting user {}", userId, e);
            throw new PersistenceException("Failed to get user");
        }
    }

    @Override
    public List<String> getAllUsers() throws PersistenceException {
        try {
            ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                    .attributesToProject("userId")
                    .build();

            return usersTable.scan(request).stream()
                    .flatMap(page -> page.items().stream())
                    .map(DDBUser::getUserId)
                    .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown getting all users", e);
            throw new PersistenceException("Failed to get all users");
        }
    }
}
