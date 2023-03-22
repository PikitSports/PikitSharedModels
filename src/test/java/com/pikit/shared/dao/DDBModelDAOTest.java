package com.pikit.shared.dao;

import com.pikit.shared.dao.ddb.DDBModelDAO;
import com.pikit.shared.dao.ddb.model.DDBModel;
import com.pikit.shared.dao.ddb.model.ModelStatus;
import com.pikit.shared.dynamodb.LocalDynamoDB;
import com.pikit.shared.enums.League;
import com.pikit.shared.exceptions.NotFoundException;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.ModelConfiguration;
import com.pikit.shared.models.ModelPerformance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DDBModelDAOTest {
    private static final String EXECUTION = "execution";
    private static final String MODEL = "model";
    private static final String USER = "USER";
    private LocalDynamoDB localDynamoDB = new LocalDynamoDB();
    private DynamoDbTable<DDBModel> modelsTable;
    private DDBModelDAO modelDAO;

    @BeforeEach
    public void setup() {
        localDynamoDB.start();

        TableSchema<DDBModel> modelTableSchema = TableSchema.fromBean(DDBModel.class);

        DynamoDbClient localDynamoClient = localDynamoDB.createClient();
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(localDynamoClient)
                .build();

        modelsTable = spy(enhancedClient.table("Models", modelTableSchema));

        modelsTable.createTable();

        modelDAO = new DDBModelDAO(modelsTable);
    }

    @Test
    public void createModel_successTest() throws PersistenceException {
        String modelId = modelDAO.createModel(USER, ModelConfiguration.builder()
                .league(League.NFL)
                .build());
        DDBModel model = modelsTable.getItem(Key.builder().partitionValue(modelId).build());
        assertThat(model).isNotNull();
        assertThat(model.getUserCreatedBy()).isEqualTo(USER);
        assertThat(model.getModelConfiguration().getLeague()).isEqualTo(League.NFL);
        assertThat(model.getModelStatus()).isEqualTo(ModelStatus.CREATING.toString());
    }

    @Test
    public void createModel_exceptionThrown() {
        doThrow(DynamoDbException.class).when(modelsTable).putItem(any(PutItemEnhancedRequest.class));
        assertThatThrownBy(() -> modelDAO.createModel(USER, ModelConfiguration.builder().build()))
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    public void updateModelConfiguration_successTest() throws PersistenceException, NotFoundException {
        String modelDescription = "model description";
        String modelId = modelDAO.createModel(USER, ModelConfiguration.builder()
                .league(League.NFL)
                .modelDescription(modelDescription)
                .build());

        modelDAO.updateModelConfiguration(modelId, ModelConfiguration.builder()
                .league(League.MLB)
                .build());

        DDBModel model = modelsTable.getItem(Key.builder()
                .partitionValue(modelId)
                .build());

        assertThat(model.getModelConfiguration().getLeague()).isEqualTo(League.MLB);
        assertThat(model.getModelConfiguration().getModelDescription()).isEqualTo(modelDescription);
    }

    @Test
    public void updateModelConfiguration_notFound() {
        assertThatThrownBy(() -> modelDAO.updateModelConfiguration("unknownModel", ModelConfiguration.builder()
                .league(League.MLB)
                .build())).isInstanceOf(NotFoundException.class);
    }

    @Test
    public void updateModelConfiguration_exceptionThrown() throws PersistenceException {
        String modelId = modelDAO.createModel(USER, ModelConfiguration.builder()
                .league(League.NFL)
                .build());

        doThrow(DynamoDbException.class).when(modelsTable).updateItem(any(UpdateItemEnhancedRequest.class));

        assertThatThrownBy(() -> modelDAO.updateModelConfiguration(modelId, ModelConfiguration.builder()
                .league(League.MLB)
                .build())).isInstanceOf(PersistenceException.class);

        DDBModel model = modelsTable.getItem(Key.builder()
                .partitionValue(modelId)
                .build());

        assertThat(model.getModelConfiguration().getLeague()).isEqualTo(League.NFL);
    }

    @Test
    public void getModel_successTest() throws PersistenceException {
        String modelId = modelDAO.createModel(USER, ModelConfiguration.builder()
                .league(League.NFL)
                .build());

        Optional<DDBModel> modelOptional = modelDAO.getModelFromId(modelId);
        assertThat(modelOptional.isPresent()).isTrue();
        assertThat(modelOptional.get().getModelConfiguration().getLeague()).isEqualTo(League.NFL);
    }

    @Test
    public void getModel_notExists() throws PersistenceException {
        Optional<DDBModel> modelOptional = modelDAO.getModelFromId("unknownModel");
        assertThat(modelOptional.isPresent()).isFalse();
    }

    @Test
    public void getModel_exceptionThrown() throws PersistenceException {
        String modelId = modelDAO.createModel(USER, ModelConfiguration.builder()
                .league(League.NFL)
                .build());

        doThrow(DynamoDbException.class).when(modelsTable).getItem(GetItemEnhancedRequest.builder()
                .key(Key.builder()
                        .partitionValue(modelId)
                        .build())
                .build());

        assertThatThrownBy(() -> modelDAO.getModelFromId(modelId)).isInstanceOf(PersistenceException.class);
    }

    @Test
    public void updateModelAfterRun_successTest() throws PersistenceException, NotFoundException {
        String modelId = modelDAO.createModel(USER, ModelConfiguration.builder()
                .league(League.NFL)
                .build());

        DDBModel currentModel = modelsTable.getItem(Key.builder()
                .partitionValue(modelId)
                .build());

        assertThat(currentModel.getModelPerformance()).isNull();

        modelDAO.updateModelAfterModelRun(modelId, ModelPerformance.builder().build());

        DDBModel newModel = modelsTable.getItem(Key.builder()
                .partitionValue(modelId)
                .build());

        assertThat(newModel.getModelPerformance()).isNotNull();
        assertThat(newModel.getModelConfiguration().getLeague()).isEqualTo(League.NFL);
    }

    @Test
    public void updateModelAfterRun_notExists() {
        assertThatThrownBy(() -> modelDAO.updateModelAfterModelRun("unknownModel", ModelPerformance.builder().build()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void updateModelAfterRun_exceptionThrown() throws PersistenceException {
        String modelId = modelDAO.createModel(USER, ModelConfiguration.builder()
                .league(League.NFL)
                .build());

        doThrow(DynamoDbException.class).when(modelsTable).updateItem(any(UpdateItemEnhancedRequest.class));

        assertThatThrownBy(() -> modelDAO.updateModelAfterModelRun(modelId, ModelPerformance.builder().build()))
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    public void deleteModel_successTest() throws PersistenceException {
        String modelId = modelDAO.createModel(USER, ModelConfiguration.builder()
                .league(League.NFL)
                .build());

        DDBModel currentModel = modelsTable.getItem(Key.builder()
                .partitionValue(modelId)
                .build());

        assertThat(currentModel).isNotNull();

        modelDAO.deleteModel(modelId);

        DDBModel newModel = modelsTable.getItem(Key.builder()
                .partitionValue(modelId)
                .build());

        assertThat(newModel).isNull();
    }

    @Test
    public void deleteModel_notExists() throws PersistenceException {
        //Doesn't throw exception
        modelDAO.deleteModel("unkownModel");
    }

    @Test
    public void deleteModel_exceptionThrown() throws PersistenceException {
        String modelId = modelDAO.createModel(USER, ModelConfiguration.builder()
                .league(League.NFL)
                .build());

        DDBModel currentModel = modelsTable.getItem(Key.builder()
                .partitionValue(modelId)
                .build());

        assertThat(currentModel).isNotNull();

        doThrow(DynamoDbException.class).when(modelsTable).deleteItem(any(DeleteItemEnhancedRequest.class));

        assertThatThrownBy(() -> modelDAO.deleteModel(modelId))
                .isInstanceOf(PersistenceException.class);

        DDBModel newModel = modelsTable.getItem(Key.builder()
                .partitionValue(modelId)
                .build());

        assertThat(newModel).isNotNull();
    }

    @Test
    public void updateAndGetModelStatus_successTest() throws PersistenceException, NotFoundException {
        String modelId = modelDAO.createModel(USER, ModelConfiguration.builder()
                .league(League.NFL)
                .build());

        ModelStatus modelStatus = modelDAO.getModelStatus(modelId);
        assertThat(modelStatus).isEqualTo(ModelStatus.CREATING);

        modelDAO.updateModelRunInformation(modelId, ModelStatus.IN_PROGRESS, EXECUTION);

        modelStatus = modelDAO.getModelStatus(modelId);
        assertThat(modelStatus).isEqualTo(ModelStatus.IN_PROGRESS);

        modelDAO.updateModelRunInformation(modelId, ModelStatus.COMPLETE, null);

        modelStatus = modelDAO.getModelStatus(modelId);
        assertThat(modelStatus).isEqualTo(ModelStatus.COMPLETE);

        DDBModel currentModel = modelsTable.getItem(Key.builder()
                .partitionValue(modelId)
                .build());

        assertThat(currentModel.getModelWorkflowExecution()).isEqualTo(EXECUTION);
    }

    @Test
    public void getModelStatus_notFound() {
        assertThatThrownBy(() -> modelDAO.getModelStatus(MODEL)).isInstanceOf(NotFoundException.class);
    }

    @Test
    public void getModelStatus_exceptionThrown() {
        doThrow(DynamoDbException.class).when(modelsTable).getItem(GetItemEnhancedRequest.builder()
                .key(Key.builder()
                        .partitionValue(MODEL)
                        .build())
                .consistentRead(true)
                .build());
        assertThatThrownBy(() -> modelDAO.getModelStatus(MODEL)).isInstanceOf(PersistenceException.class);
    }

    @Test
    public void updateModelRunInformation_notFound() {
        assertThatThrownBy(() -> modelDAO.updateModelRunInformation(MODEL, ModelStatus.COMPLETE, null))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void updateModelRunInformation_exceptionThrown() {
        doThrow(DynamoDbException.class).when(modelsTable).updateItem(any(UpdateItemEnhancedRequest.class));
        assertThatThrownBy(() -> modelDAO.updateModelRunInformation(MODEL, ModelStatus.IN_PROGRESS, null))
                .isInstanceOf(PersistenceException.class);
    }
}
