package com.pikit.shared.dao.ddb;

import com.pikit.shared.dao.GamesThatMeetModelDAO;
import com.pikit.shared.dao.ddb.model.DDBGamesThatMeetModel;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.GameThatMeetsModel;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class DDBGamesThatMeetModelDAO implements GamesThatMeetModelDAO {
    private DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<DDBGamesThatMeetModel> gamesThatMeetModelTable;
    private DynamoDbIndex<DDBGamesThatMeetModel> modelIdIndex;

    public DDBGamesThatMeetModelDAO(DynamoDbEnhancedClient enhancedClient,
                                    DynamoDbTable<DDBGamesThatMeetModel> gamesThatMeetModelTable,
                                    DynamoDbIndex<DDBGamesThatMeetModel> modelIdIndex) {
        this.enhancedClient = enhancedClient;
        this.gamesThatMeetModelTable = gamesThatMeetModelTable;
        this.modelIdIndex = modelIdIndex;
    }

    @Override
    public void addGamesThatMeetModel(String modelId, Map<String, List<GameThatMeetsModel>> gamesThatMeetModel) throws PersistenceException {
        try {
            List<WriteBatch> writeBatchList = new ArrayList<>();

            for (Map.Entry<String, List<GameThatMeetsModel>> entry: gamesThatMeetModel.entrySet()) {
                String season = entry.getKey();
                List<GameThatMeetsModel> gamesThatMeetModelList = entry.getValue();
                String key = modelId + "|" + season;

                DDBGamesThatMeetModel gamesThatMeetModelItem = DDBGamesThatMeetModel.builder()
                        .id(key)
                        .games(gamesThatMeetModelList)
                        .modelId(modelId)
                        .season(season)
                        .build();

                writeBatchList.add(WriteBatch.builder(DDBGamesThatMeetModel.class)
                        .addPutItem(PutItemEnhancedRequest.builder(DDBGamesThatMeetModel.class)
                                .item(gamesThatMeetModelItem)
                                .build())
                        .mappedTableResource(gamesThatMeetModelTable)
                        .build());
            }

            enhancedClient.batchWriteItem(BatchWriteItemEnhancedRequest.builder()
                    .writeBatches(writeBatchList)
                    .build());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown adding games that meet model {}", modelId, e);
            throw new PersistenceException("Failed to add games that meet model");
        }
    }

    @Override
    public void addGamesThatMeetModel(String modelId, String season, List<GameThatMeetsModel> gamesThatMeetModel) throws PersistenceException {
        try {
            String key = modelId + "|" + season;

            DDBGamesThatMeetModel gamesThatMeetModelItem = DDBGamesThatMeetModel.builder()
                    .id(key)
                    .games(gamesThatMeetModel)
                    .modelId(modelId)
                    .season(season)
                    .build();

            gamesThatMeetModelTable.putItem(PutItemEnhancedRequest.builder(DDBGamesThatMeetModel.class)
                    .item(gamesThatMeetModelItem)
                    .build());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown adding games that meet model {} for season {}", modelId, season, e);
            throw new PersistenceException("Failed to add games that meet model for season");
        }
    }

    @Override
    public void deleteOldGamesThatMetModel(String modelId, String season) throws PersistenceException {
        try {
            String key = modelId + "|" + season;
            gamesThatMeetModelTable.deleteItem(Key.builder()
                            .partitionValue(key)
                            .build());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown deleting games that meet model {} in season {}", modelId, season, e);
            throw new PersistenceException("Failed to delete games that meet model");
        }
    }

    @Override
    public List<DDBGamesThatMeetModel> getGamesThatMeetModel(String modelId) throws PersistenceException {
        try {
            QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .partitionValue(modelId)
                            .build()))
                    .build();

            return modelIdIndex.query(request)
                    .stream()
                    .flatMap(page -> page.items().stream())
                    .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown getting games that meet model {}", modelId, e);
            throw new PersistenceException("Failed to get games that meet model");
        }
    }
}
