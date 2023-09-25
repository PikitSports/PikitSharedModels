package com.pikit.shared.dao;

import com.pikit.shared.dao.ddb.DDBModelDAO;
import com.pikit.shared.dao.ddb.model.DDBModel;
import com.pikit.shared.dao.ddb.model.ModelStatus;
import com.pikit.shared.datasource.DataSourceConstants;
import com.pikit.shared.dynamodb.LocalDynamoDB;
import com.pikit.shared.enums.League;
import com.pikit.shared.enums.ModelTimeRange;
import com.pikit.shared.exceptions.NotFoundException;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.ModelConfiguration;
import com.pikit.shared.models.ModelPerformance;
import com.pikit.shared.models.ModelProfitabilityStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DDBModelDAOTest {
    private static final String EXECUTION = "execution";
    private static final String MODEL = "model";
    private static final String USER = "USER";
    private static final Double LAST_10 = 55.0;
    private static final Double LAST_50 = 50.0;
    private static final Double LAST_100 = 60.0;
    private LocalDynamoDB localDynamoDB = new LocalDynamoDB();
    private DynamoDbTable<DDBModel> modelsTable;
    private DynamoDbIndex<DDBModel> userModelsIndex;
    private DynamoDbIndex<DDBModel> leagueModelsIndex;
    private DynamoDbIndex<DDBModel> last10GamesModelsIndex;
    private DynamoDbIndex<DDBModel> last50GamesModelsIndex;
    private DynamoDbIndex<DDBModel> last100GamesModelsIndex;
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

        EnhancedGlobalSecondaryIndex userIndex = EnhancedGlobalSecondaryIndex.builder()
                .indexName("userModelsIndex")
                .projection(Projection.builder()
                        .projectionType(ProjectionType.ALL)
                        .build())
                .build();

        EnhancedGlobalSecondaryIndex leagueIndex = EnhancedGlobalSecondaryIndex.builder()
                .indexName("leagueIndex")
                .projection(Projection.builder()
                        .projectionType(ProjectionType.ALL)
                        .build())
                .build();

        EnhancedGlobalSecondaryIndex last10GamesIndex = EnhancedGlobalSecondaryIndex.builder()
                .indexName("last10GamesIndex")
                .projection(Projection.builder()
                        .projectionType(ProjectionType.ALL)
                        .build())
                .build();

        EnhancedGlobalSecondaryIndex last50GamesIndex = EnhancedGlobalSecondaryIndex.builder()
                .indexName("last50GamesIndex")
                .projection(Projection.builder()
                        .projectionType(ProjectionType.ALL)
                        .build())
                .build();

        EnhancedGlobalSecondaryIndex last100GamesIndex = EnhancedGlobalSecondaryIndex.builder()
                .indexName("last100GamesIndex")
                .projection(Projection.builder()
                        .projectionType(ProjectionType.ALL)
                        .build())
                .build();

        modelsTable.createTable(CreateTableEnhancedRequest.builder()
                .globalSecondaryIndices(userIndex, leagueIndex, last10GamesIndex, last50GamesIndex, last100GamesIndex)
                .build());

        userModelsIndex = spy(modelsTable.index("userModelsIndex"));
        leagueModelsIndex = spy(modelsTable.index("leagueIndex"));
        last10GamesModelsIndex = spy(modelsTable.index("last10GamesIndex"));
        last50GamesModelsIndex = spy(modelsTable.index("last50GamesIndex"));
        last100GamesModelsIndex = spy(modelsTable.index("last100GamesIndex"));

        modelDAO = new DDBModelDAO(modelsTable, userModelsIndex, leagueModelsIndex, last10GamesModelsIndex, last50GamesModelsIndex, last100GamesModelsIndex);
    }

    @Test
    public void createModel_successTest() throws PersistenceException {
        String modelId = modelDAO.createModel(USER, ModelConfiguration.builder()
                .league(League.NFL)
                .timeRange(ModelTimeRange.LAST_3_SEASONS)
                .build());
        DDBModel model = modelsTable.getItem(Key.builder().partitionValue(modelId).build());
        assertThat(model).isNotNull();
        assertThat(model.getUserCreatedBy()).isEqualTo(USER);
        assertThat(model.getModelConfiguration().getLeague()).isEqualTo(League.NFL);
        assertThat(model.getModelStatus()).isEqualTo(ModelStatus.CREATING);
        assertThat(model.getModelConfiguration().getSeasonsStored())
                .isEqualTo(DataSourceConstants.getModelSeasons(League.NFL, ModelTimeRange.LAST_3_SEASONS));
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
    public void getModelsForUser_successTest() throws PersistenceException {
        String modelId1 = modelDAO.createModel(USER, ModelConfiguration.builder()
                .league(League.NFL)
                .build());

        String modelId2 = modelDAO.createModel(USER, ModelConfiguration.builder()
                .league(League.NFL)
                .build());

        //Create 3rd model not created by user just to ensure accuracy.
        modelDAO.createModel("notUser", ModelConfiguration.builder()
                .league(League.NFL)
                .build());

        List<DDBModel> modelsForUser = modelDAO.getModelsForUser(USER);
        assertThat(modelsForUser.size()).isEqualTo(2);
        assertThat(modelsForUser.get(0).getModelId()).isEqualTo(modelId1);
        assertThat(modelsForUser.get(1).getModelId()).isEqualTo(modelId2);
    }

    @Test
    public void getModelsForUser_noModels() throws PersistenceException {
        List<DDBModel> modelsForUser = modelDAO.getModelsForUser(USER);
        assertThat(modelsForUser).isEmpty();
    }

    @Test
    public void getModelsForUser_exceptionThrown() {
        doThrow(DynamoDbException.class).when(userModelsIndex).query(any(QueryEnhancedRequest.class));

        assertThatThrownBy(() -> modelDAO.getModelsForUser(USER))
                .isInstanceOf(PersistenceException.class);
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

        modelDAO.updateModelAfterModelRun(modelId, ModelPerformance.builder().build(), getModelProfitabilityStats());

        DDBModel newModel = modelsTable.getItem(Key.builder()
                .partitionValue(modelId)
                .build());

        assertThat(newModel.getModelPerformance()).isNotNull();
        assertThat(newModel.getModelConfiguration().getLeague()).isEqualTo(League.NFL);
        assertThat(newModel.getLast10Games()).isEqualTo(LAST_10);
        assertThat(newModel.getLast50Games()).isEqualTo(LAST_50);
        assertThat(newModel.getLast100Games()).isEqualTo(LAST_100);
    }

    @Test
    public void updateModelAfterRun_notExists() {
        assertThatThrownBy(() -> modelDAO.updateModelAfterModelRun("unknownModel",
                ModelPerformance.builder().build(),
                getModelProfitabilityStats()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void updateModelAfterRun_exceptionThrown() throws PersistenceException {
        String modelId = modelDAO.createModel(USER, ModelConfiguration.builder()
                .league(League.NFL)
                .build());

        doThrow(DynamoDbException.class).when(modelsTable).updateItem(any(UpdateItemEnhancedRequest.class));

        assertThatThrownBy(() -> modelDAO.updateModelAfterModelRun(modelId,
                ModelPerformance.builder().build(),
                getModelProfitabilityStats()))
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

    @Test
    public void getModelsForLeague_successTest() throws PersistenceException {
        String modelId1 = modelDAO.createModel(USER, ModelConfiguration.builder()
                .league(League.NFL)
                .build());

        String modelId2 = modelDAO.createModel(USER, ModelConfiguration.builder()
                .league(League.NFL)
                .build());

        //Create 3rd model not in NFL just to ensure accuracy.
        modelDAO.createModel(USER, ModelConfiguration.builder()
                .league(League.MLB)
                .build());

        List<DDBModel> modelsForLeague = modelDAO.getModelsForLeague(League.NFL);
        assertThat(modelsForLeague.size()).isEqualTo(2);
        assertThat(modelsForLeague.get(0).getModelId()).isEqualTo(modelId1);
        assertThat(modelsForLeague.get(1).getModelId()).isEqualTo(modelId2);
    }

    @Test
    public void getModelsForLeague_noModels() throws PersistenceException {
        List<DDBModel> modelsForLeague = modelDAO.getModelsForLeague(League.NFL);
        assertThat(modelsForLeague).isEmpty();
    }

    @Test
    public void getModelsForLeague_exceptionThrown() {
        doThrow(DynamoDbException.class).when(leagueModelsIndex).query(any(QueryEnhancedRequest.class));

        assertThatThrownBy(() -> modelDAO.getModelsForLeague(League.NFL))
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    public void getTopModelsForLeague_successTest() throws PersistenceException, NotFoundException {
        String modelId = modelDAO.createModel(USER, ModelConfiguration.builder()
                .league(League.NFL)
                .build());

        modelDAO.updateModelAfterModelRun(modelId, ModelPerformance.builder().build(), getModelProfitabilityStats());

        List<DDBModel> top10Models = modelDAO.getTopModelsFromLast10Games(League.NFL, 100);
        List<DDBModel> top50Models = modelDAO.getTopModelsFromLast50Games(League.NFL, 100);
        List<DDBModel> top100Models = modelDAO.getTopModelsFromLast100Games(League.NFL, 100);

        assertThat(top10Models.size()).isEqualTo(1);
        assertThat(top50Models.size()).isEqualTo(1);
        assertThat(top100Models.size()).isEqualTo(1);
    }

    private ModelProfitabilityStats getModelProfitabilityStats() {
        return ModelProfitabilityStats.builder()
                .last10GamesUnits(LAST_10)
                .last50GamesUnits(LAST_50)
                .last100GamesUnits(LAST_100)
                .last10GamesWinPercentage(LAST_10)
                .last50GamesWinPercentage(LAST_50)
                .last100GamesWinPercentage(LAST_100)
                .build();
    }
}
