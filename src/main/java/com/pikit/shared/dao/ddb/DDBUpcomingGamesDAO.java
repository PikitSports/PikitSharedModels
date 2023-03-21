package com.pikit.shared.dao.ddb;

import com.pikit.shared.dao.UpcomingGamesDAO;
import com.pikit.shared.dao.ddb.model.DDBUpcomingGame;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.UpcomingGameThatMeetsModel;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class DDBUpcomingGamesDAO implements UpcomingGamesDAO {
    private DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<DDBUpcomingGame> upcomingGamesTable;
    private DynamoDbIndex<DDBUpcomingGame> modelIdIndex;

    public DDBUpcomingGamesDAO(DynamoDbEnhancedClient enhancedClient, DynamoDbTable<DDBUpcomingGame> upcomingGamesTable, DynamoDbIndex<DDBUpcomingGame> modelIdIndex) {
        this.enhancedClient = enhancedClient;
        this.upcomingGamesTable = upcomingGamesTable;
        this.modelIdIndex = modelIdIndex;
    }

    @Override
    public void addUpcomingGamesForModel(String modelId, String gameId, UpcomingGameThatMeetsModel upcomingGame) throws PersistenceException {
        try {
            upcomingGamesTable.putItem(PutItemEnhancedRequest.builder(DDBUpcomingGame.class)
                    .item(DDBUpcomingGame.builder()
                            .gameId(gameId)
                            .modelId(modelId)
                            .upcomingGame(upcomingGame)
                            .build())
                    .build());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown adding upcoming game {} for model {}", gameId, modelId, e);
            throw new PersistenceException("Failed to add upcoming game to model");
        }
    }

    @Override
    public List<UpcomingGameThatMeetsModel> getUpcomingGamesForModel(String modelId) throws PersistenceException {
        try {
            QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .partitionValue(modelId)
                            .build()))
                    .build();

            return modelIdIndex.query(request)
                    .stream()
                    .flatMap(page -> page.items().stream())
                    .collect(Collectors.toList())
                    .stream()
                    .map(DDBUpcomingGame::getUpcomingGame)
                    .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown getting upcoming games for model {} {}", modelId, e.getMessage(), e);
            throw new PersistenceException("Failed to get upcoming games: " + e.getMessage());
        }
    }

    @Override
    public void deleteUpcomingGameForModel(String modelId, String gameId) throws PersistenceException {
        try {
            upcomingGamesTable.deleteItem(Key.builder()
                    .partitionValue(gameId)
                    .sortValue(modelId)
                    .build());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown deleting upcoming game for model {}", modelId, e);
            throw new PersistenceException("Failed to delete upcoming game for model");
        }
    }

    @Override
    public void deleteAllUpcomingGamesForModel(String modelId) throws PersistenceException {
        try {
            List<UpcomingGameThatMeetsModel> upcomingGames = getUpcomingGamesForModel(modelId);
            List<WriteBatch> batchList = new ArrayList<>();

            for (UpcomingGameThatMeetsModel upcomingGame: upcomingGames) {
                batchList.add(WriteBatch.builder(DDBUpcomingGame.class)
                                .addDeleteItem(Key.builder()
                                        .partitionValue(upcomingGame.getGameId())
                                        .sortValue(modelId)
                                        .build())
                                .mappedTableResource(upcomingGamesTable)
                        .build());
            }

            enhancedClient.batchWriteItem(BatchWriteItemEnhancedRequest.builder()
                    .writeBatches(batchList)
                    .build());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown deleting upcoming games for model {}", modelId, e);
            throw new PersistenceException("Failed to delete upcoming games");
        }
    }
}
