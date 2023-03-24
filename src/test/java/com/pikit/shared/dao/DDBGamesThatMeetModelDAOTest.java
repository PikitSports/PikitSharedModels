package com.pikit.shared.dao;

import com.pikit.shared.dao.ddb.DDBGamesThatMeetModelDAO;
import com.pikit.shared.dao.ddb.model.DDBGamesThatMeetModel;
import com.pikit.shared.dynamodb.LocalDynamoDB;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.GameThatMeetsModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DDBGamesThatMeetModelDAOTest {
    private static final String MODEL_ID = "modelId";
    private static final String SEASON = "season";
    private static final String GAME = "game";
    private LocalDynamoDB localDynamoDB = new LocalDynamoDB();
    private DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<DDBGamesThatMeetModel> gamesThatMeetModelTable;
    private DynamoDbIndex<DDBGamesThatMeetModel> modelIdIndex;
    private DDBGamesThatMeetModelDAO gamesThatMeetModelDAO;

    @Mock
    private DynamoDbEnhancedClient mockedEnhancedClient;

    @BeforeEach
    public void setup() {
        localDynamoDB.start();

        TableSchema<DDBGamesThatMeetModel> gamesThatMeetModelTableSchema = TableSchema.fromBean(DDBGamesThatMeetModel.class);

        DynamoDbClient localDynamoClient = localDynamoDB.createClient();
        enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(localDynamoClient)
                .build();

        gamesThatMeetModelTable = spy(enhancedClient.table("GamesThatMeetModel", gamesThatMeetModelTableSchema));

        EnhancedGlobalSecondaryIndex modelIndex = EnhancedGlobalSecondaryIndex.builder()
                .indexName("modelIdIndex")
                .projection(Projection.builder()
                        .projectionType(ProjectionType.ALL)
                        .build())
                .build();

        gamesThatMeetModelTable.createTable(CreateTableEnhancedRequest.builder()
                        .globalSecondaryIndices(modelIndex)
                        .build());

        modelIdIndex = spy(gamesThatMeetModelTable.index("modelIdIndex"));

        gamesThatMeetModelDAO = new DDBGamesThatMeetModelDAO(enhancedClient, gamesThatMeetModelTable, modelIdIndex);
    }

    @Test
    public void addGamesThatMeetModel_successTest() throws PersistenceException {
        gamesThatMeetModelDAO.addGamesThatMeetModel(MODEL_ID, getListOfGamesThatMeetModel());

        List<DDBGamesThatMeetModel> gamesThatMeetModel = callDynamoToGetGamesThatMeetModel(MODEL_ID);

        assertThat(gamesThatMeetModel).isNotNull();
        assertThat(gamesThatMeetModel.size()).isEqualTo(2);
        assertThat(gamesThatMeetModel.get(0).getModelId()).isEqualTo(MODEL_ID);
        assertThat(gamesThatMeetModel.get(0).getSeason()).isEqualTo(SEASON);
        assertThat(gamesThatMeetModel.get(0).getGames().get(0).getGameId()).isEqualTo(GAME);
    }

    @Test
    public void addGamesThatMeetModel_exceptionThrown() {
        DDBGamesThatMeetModelDAO mockedDao = new DDBGamesThatMeetModelDAO(mockedEnhancedClient, gamesThatMeetModelTable, modelIdIndex);

        doThrow(DynamoDbException.class).when(mockedEnhancedClient).batchWriteItem(any(BatchWriteItemEnhancedRequest.class));

        assertThatThrownBy(() -> mockedDao.addGamesThatMeetModel(MODEL_ID, getListOfGamesThatMeetModel()))
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    public void deleteGamesThatMeetModel_success() throws PersistenceException {
        gamesThatMeetModelDAO.addGamesThatMeetModel(MODEL_ID, getListOfGamesThatMeetModel());
        List<DDBGamesThatMeetModel> gamesThatMeetModel = callDynamoToGetGamesThatMeetModel(MODEL_ID);
        assertThat(gamesThatMeetModel.size()).isEqualTo(2);

        gamesThatMeetModelDAO.deleteOldGamesThatMetModel(MODEL_ID, SEASON);
        gamesThatMeetModel = callDynamoToGetGamesThatMeetModel(MODEL_ID);
        assertThat(gamesThatMeetModel.size()).isEqualTo(1);
    }

    @Test
    public void deleteGamesThatMeetModel_exceptionThrown() throws PersistenceException {
        gamesThatMeetModelDAO.addGamesThatMeetModel(MODEL_ID, getListOfGamesThatMeetModel());
        List<DDBGamesThatMeetModel> gamesThatMeetModel = callDynamoToGetGamesThatMeetModel(MODEL_ID);
        assertThat(gamesThatMeetModel.size()).isEqualTo(2);

        doThrow(DynamoDbException.class).when(gamesThatMeetModelTable).deleteItem(any(Key.class));

        assertThatThrownBy(() -> gamesThatMeetModelDAO.deleteOldGamesThatMetModel(MODEL_ID, SEASON))
                .isInstanceOf(PersistenceException.class);

        gamesThatMeetModel = callDynamoToGetGamesThatMeetModel(MODEL_ID);
        assertThat(gamesThatMeetModel.size()).isEqualTo(2);
    }

    @Test
    public void getGamesThatMeetModel_success() throws PersistenceException {
        gamesThatMeetModelDAO.addGamesThatMeetModel(MODEL_ID, getListOfGamesThatMeetModel());

        List<DDBGamesThatMeetModel> gamesThatMeetModel = gamesThatMeetModelDAO.getGamesThatMeetModel(MODEL_ID);
        assertThat(gamesThatMeetModel).isNotNull();
        assertThat(gamesThatMeetModel.size()).isEqualTo(2);
        assertThat(gamesThatMeetModel.get(0).getModelId()).isEqualTo(MODEL_ID);
        assertThat(gamesThatMeetModel.get(0).getId()).isEqualTo(MODEL_ID + "|" + SEASON);
        assertThat(gamesThatMeetModel.get(0).getSeason()).isEqualTo(SEASON);
        assertThat(gamesThatMeetModel.get(0).getGames().size()).isEqualTo(1);
        assertThat(gamesThatMeetModel.get(0).getGames().get(0).getGameId()).isEqualTo(GAME);
    }

    @Test
    public void getGamesThatMeetModel_noneExist() throws PersistenceException {

        List<DDBGamesThatMeetModel> gamesThatMeetModel = gamesThatMeetModelDAO.getGamesThatMeetModel(MODEL_ID);
        assertThat(gamesThatMeetModel).isNotNull();
        assertThat(gamesThatMeetModel.size()).isEqualTo(0);
    }

    @Test
    public void getGamesThatMeetModel_exceptionThrown() throws PersistenceException {
        gamesThatMeetModelDAO.addGamesThatMeetModel(MODEL_ID, getListOfGamesThatMeetModel());

        doThrow(DynamoDbException.class).when(modelIdIndex).query(any(QueryEnhancedRequest.class));

        assertThatThrownBy(() -> gamesThatMeetModelDAO.getGamesThatMeetModel(MODEL_ID))
                .isInstanceOf(PersistenceException.class);
    }

    private List<DDBGamesThatMeetModel> callDynamoToGetGamesThatMeetModel(String modelId) {
        SdkIterable<Page<DDBGamesThatMeetModel>> response = modelIdIndex.query(QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(MODEL_ID).build()))
                .build());

        return response.stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }

    private TreeMap<String, List<GameThatMeetsModel>> getListOfGamesThatMeetModel() {
        TreeMap<String, List<GameThatMeetsModel>> gamesThatMeetModel = new TreeMap<>();
        List<GameThatMeetsModel> games = new ArrayList<>();
        games.add(GameThatMeetsModel.builder()
                .gameId(GAME)
                .build());
        gamesThatMeetModel.put(SEASON, games);
        gamesThatMeetModel.put("2023", games);
        return gamesThatMeetModel;
    }
}
