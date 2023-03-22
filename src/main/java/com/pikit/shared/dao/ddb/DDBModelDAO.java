package com.pikit.shared.dao.ddb;

import com.pikit.shared.dao.ModelDAO;
import com.pikit.shared.dao.ddb.model.ModelStatus;
import com.pikit.shared.exceptions.NotFoundException;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.ModelConfiguration;
import com.pikit.shared.models.ModelPerformance;
import com.pikit.shared.dao.ddb.model.DDBModel;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class DDBModelDAO implements ModelDAO {

    private DynamoDbTable<DDBModel> modelsTable;

    public DDBModelDAO(DynamoDbTable<DDBModel> modelsTable) {
        this.modelsTable = modelsTable;
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
                .modelStatus(ModelStatus.CREATING.toString())
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
    public void updateModelAfterModelRun(String modelId, ModelPerformance modelPerformance) throws PersistenceException, NotFoundException{
        try {
            DDBModel modelToUpdate = DDBModel.builder()
                    .modelId(modelId)
                    .modelPerformance(modelPerformance)
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

            return ModelStatus.valueOf(model.getModelStatus());
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
                    .modelStatus(modelStatus.toString())
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
