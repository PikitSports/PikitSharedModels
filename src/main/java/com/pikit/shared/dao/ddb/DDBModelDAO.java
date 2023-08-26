package com.pikit.shared.dao.ddb;

import com.pikit.shared.dao.ModelDAO;
import com.pikit.shared.dao.ddb.model.ModelStatus;
import com.pikit.shared.enums.League;
import com.pikit.shared.exceptions.NotFoundException;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.ModelConfiguration;
import com.pikit.shared.models.ModelPerformance;
import com.pikit.shared.dao.ddb.model.DDBModel;
import com.pikit.shared.models.ModelProfitabilityStats;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
public class DDBModelDAO implements ModelDAO {

    private final DynamoDbTable<DDBModel> modelsTable;
    private final DynamoDbIndex<DDBModel> userModelsIndex;
    private final DynamoDbIndex<DDBModel> leagueIndex;
    private final DynamoDbIndex<DDBModel> last10GamesIndex;
    private final DynamoDbIndex<DDBModel> last50GamesIndex;
    private final DynamoDbIndex<DDBModel> last100GamesIndex;

    public DDBModelDAO(DynamoDbTable<DDBModel> modelsTable,
                       DynamoDbIndex<DDBModel> userModelsIndex,
                       DynamoDbIndex<DDBModel> leagueIndex,
                       DynamoDbIndex<DDBModel> last10GamesIndex,
                       DynamoDbIndex<DDBModel> last50GamesIndex,
                       DynamoDbIndex<DDBModel> last100GamesIndex) {
        this.modelsTable = modelsTable;
        this.userModelsIndex = userModelsIndex;
        this.leagueIndex = leagueIndex;
        this.last10GamesIndex = last10GamesIndex;
        this.last50GamesIndex = last50GamesIndex;
        this.last100GamesIndex = last100GamesIndex;
    }

    @Override
    public String createModel(String userId, ModelConfiguration modelConfiguration) throws PersistenceException {
        String modelId = UUID.randomUUID().toString().replace("-", "");
        long creationTimestamp = System.currentTimeMillis();

        DDBModel modelToSave = DDBModel.builder()
                .modelId(modelId)
                .userCreatedBy(userId)
                .creationTimestamp(creationTimestamp)
                .modelConfiguration(modelConfiguration)
                .modelStatus(ModelStatus.CREATING)
                .league(modelConfiguration.getLeague())
                .build();

        try {
            modelsTable.putItem(modelToSave);
            return modelId;
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown creating model for {}", userId, e);
            throw new PersistenceException("Failed to save model");
        }
    }

    @Override
    public void updateModelConfiguration(String modelId, ModelConfiguration modelConfiguration) throws PersistenceException, NotFoundException {
        try {
            Optional<DDBModel> modelOptional = getModelFromId(modelId);

            if (!modelOptional.isPresent()) {
                log.error("Model attempting to update was not found {}", modelId);
                throw new NotFoundException("Model not found");
            }

            DDBModel modelToUpdate = DDBModel.builder()
                    .modelId(modelId)
                    .lastUpdatedTimestamp(System.currentTimeMillis())
                    .modelConfiguration(mergeModelConfiguration(modelOptional.get().getModelConfiguration(), modelConfiguration))
                    .build();

            UpdateItemEnhancedRequest<DDBModel> request = UpdateItemEnhancedRequest.builder(DDBModel.class)
                    .item(modelToUpdate)
                    .ignoreNulls(true)
                    .build();

            modelsTable.updateItem(request);
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown updating model {}", modelId, e);
            throw new PersistenceException("Failed to update model");
        }
    }

    @Override
    public Optional<DDBModel> getModelFromId(String modelId) throws PersistenceException {
        try {
            GetItemEnhancedRequest request = GetItemEnhancedRequest.builder()
                    .key(Key.builder()
                            .partitionValue(modelId)
                            .build())
                    .build();

            DDBModel model = modelsTable.getItem(request);

            if (model != null) {
                return Optional.of(model);
            } else {
                return Optional.empty();
            }
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown retrieving model {}", modelId, e);
            throw new PersistenceException("Failed to get model");
        }
    }

    @Override
    public List<DDBModel> getModelsForUser(String user) throws PersistenceException {
        try {
            QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .partitionValue(user)
                            .build()))
                    .build();

            return userModelsIndex.query(request)
                    .stream()
                    .flatMap(page -> page.items().stream())
                    .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown retrieving models for user {}", user, e);
            throw new PersistenceException("Failed to get models for user");
        }
    }

    @Override
    public void updateModelAfterModelRun(String modelId, ModelPerformance modelPerformance, ModelProfitabilityStats modelProfitabilityStats)
            throws PersistenceException, NotFoundException{
        try {
            DDBModel modelToUpdate = DDBModel.builder()
                    .modelId(modelId)
                    .modelPerformance(modelPerformance)
                    .last10Games(modelProfitabilityStats.getLast10Games())
                    .last50Games(modelProfitabilityStats.getLast50Games())
                    .last100Games(modelProfitabilityStats.getLast100Games())
                    .build();

            UpdateItemEnhancedRequest<DDBModel> request = UpdateItemEnhancedRequest.builder(DDBModel.class)
                    .item(modelToUpdate)
                    .conditionExpression(Expression.builder()
                            .expression("attribute_exists(#modelId)")
                            .putExpressionName("#modelId", "modelId")
                            .build())
                    .ignoreNulls(true)
                    .build();

            modelsTable.updateItem(request);
        } catch (ConditionalCheckFailedException e) {
            log.error("[DynamoDB] Conditional check failed while updating model {}", modelId, e);
            throw new NotFoundException("Model not found");
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown updating model {}", modelId, e);
            throw new PersistenceException("Failed to update model");
        }
    }

    @Override
    public void deleteModel(String modelId) throws PersistenceException {
        try {
            DeleteItemEnhancedRequest request = DeleteItemEnhancedRequest.builder()
                    .key(Key.builder()
                            .partitionValue(modelId)
                            .build())
                    .build();
            modelsTable.deleteItem(request);
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown deleting model {}", modelId, e);
            throw new PersistenceException("Failed to delete model");
        }
    }

    @Override
    public ModelStatus getModelStatus(String modelId) throws PersistenceException, NotFoundException {
        try {
            GetItemEnhancedRequest request = GetItemEnhancedRequest.builder()
                    .key(Key.builder().partitionValue(modelId).build())
                    .consistentRead(true)
                    .build();

            DDBModel model = modelsTable.getItem(request);
            if (model == null) {
                throw new NotFoundException("Model not found");
            }

            return model.getModelStatus();
        } catch (DynamoDbException | IllegalArgumentException e) {
            log.error("[DynamoDB] Exception thrown getting model status {}", modelId, e);
            throw new PersistenceException("Failed to get model status");
        }
    }

    @Override
    public void updateModelRunInformation(String modelId, ModelStatus modelStatus, String executionId)
            throws PersistenceException, NotFoundException {
        try {
            DDBModel modelToUpdate = DDBModel.builder()
                    .modelId(modelId)
                    .modelStatus(modelStatus)
                    .modelWorkflowExecution(executionId)
                    .build();

            UpdateItemEnhancedRequest<DDBModel> request = UpdateItemEnhancedRequest.builder(DDBModel.class)
                    .item(modelToUpdate)
                    .conditionExpression(Expression.builder()
                            .expression("attribute_exists(#modelId)")
                            .putExpressionName("#modelId", "modelId")
                            .build())
                    .ignoreNulls(true)
                    .build();

            modelsTable.updateItem(request);
        } catch (ConditionalCheckFailedException e) {
            log.error("[DynamoDB] Conditional check failed when updating model run information for model {}", modelId, e);
            throw new NotFoundException("Model not found");
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown updating model run information for model {}", modelId, e);
            throw new PersistenceException("Failed to update model run information");
        }
    }

    @Override
    public List<DDBModel> getModelsForLeague(League league) throws PersistenceException {
        try {
            QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .partitionValue(league.toString())
                            .build()))
                    .build();

            return leagueIndex.query(request)
                    .stream()
                    .flatMap(page -> page.items().stream())
                    .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown retrieving models for league {}", league, e);
            throw new PersistenceException("Failed to get models for league");
        }
    }

    @Override
    public List<DDBModel> getTopModelsFromLast10Games(League league, int pageSize) throws PersistenceException {
        try {
            QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .partitionValue(league.toString())
                            .build()))
                    .build();

            return last10GamesIndex.query(request)
                    .stream()
                    .flatMap(page -> page.items().stream())
                    .limit(pageSize)
                    .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            log.error("[DynamoDb] Exception thrown getting top models from last 10 games for {}", league, e);
            throw new PersistenceException("Failed to get top models from last 10 games");
        }
    }

    @Override
    public List<DDBModel> getTopModelsFromLast50Games(League league, int pageSize) throws PersistenceException {
        try {
            QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .partitionValue(league.toString())
                            .build()))
                    .build();

            return last50GamesIndex.query(request)
                    .stream()
                    .flatMap(page -> page.items().stream())
                    .limit(pageSize)
                    .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            log.error("[DynamoDb] Exception thrown getting top models from last 50 games for {}", league, e);
            throw new PersistenceException("Failed to get top models from last 50 games");
        }
    }

    @Override
    public List<DDBModel> getTopModelsFromLast100Games(League league, int pageSize) throws PersistenceException {
        try {
            QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .partitionValue(league.toString())
                            .build()))
                    .build();

            return last100GamesIndex.query(request)
                    .stream()
                    .flatMap(page -> page.items().stream())
                    .limit(pageSize)
                    .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            log.error("[DynamoDb] Exception thrown getting top models from last 100 games for {}", league, e);
            throw new PersistenceException("Failed to get top models from last 100 games");
        }
    }

    /*
        Given an existing model config and an updated model config, merge them together so that all fields that should
        be updated during an Update request will be added to the existing config, and so that we don't lose any of the
        existing fields.
     */
    private ModelConfiguration mergeModelConfiguration(ModelConfiguration currentConfig, ModelConfiguration newConfig) {
        return ModelConfiguration.builder()
                .modelDescription(newConfig.getModelDescription() != null ? newConfig.getModelDescription() : currentConfig.getModelDescription())
                .league(newConfig.getLeague() != null ? newConfig.getLeague() : currentConfig.getLeague())
                .betType(newConfig.getBetType() != null ? newConfig.getBetType() : currentConfig.getBetType())
                .seasonsStored(newConfig.getSeasonsStored() != null ? newConfig.getSeasonsStored() : currentConfig.getSeasonsStored())
                .betsTaken(newConfig.getBetsTaken() != null ? newConfig.getBetsTaken() : currentConfig.getBetsTaken())
                .modelRequirements(newConfig.getModelRequirements() != null ? newConfig.getModelRequirements() : currentConfig.getModelRequirements())
                .build();
    }
}
