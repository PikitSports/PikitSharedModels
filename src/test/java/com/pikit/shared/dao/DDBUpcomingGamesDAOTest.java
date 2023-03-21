package com.pikit.shared.dao;

import com.pikit.shared.dao.ddb.DDBGamesThatMeetModelDAO;
import com.pikit.shared.dao.ddb.DDBUpcomingGamesDAO;
import com.pikit.shared.dao.ddb.model.DDBGamesThatMeetModel;
import com.pikit.shared.dao.ddb.model.DDBUpcomingGame;
import com.pikit.shared.dynamodb.LocalDynamoDB;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.UpcomingGameThatMeetsModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;

import java.util.List;

import static org.mockito.Mockito.spy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DDBUpcomingGamesDAOTest {
    private static final String MODEL_ID = "modelId";
    private static final String GAME_ID = "gameId";
    private LocalDynamoDB localDynamoDB = new LocalDynamoDB();
    private DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<DDBUpcomingGame> upcomingGamesTable;
    private DynamoDbIndex<DDBUpcomingGame> modelIdIndex;
    private DDBUpcomingGamesDAO upcomingGamesDAO;

    @Mock
    private DynamoDbEnhancedClient mockedEnhancedClient;

    @BeforeEach
    public void setup() {
        localDynamoDB.start();

        TableSchema<DDBUpcomingGame> upcomingGameTableSchema = TableSchema.fromBean(DDBUpcomingGame.class);

        DynamoDbClient localDynamoClient = localDynamoDB.createClient();
        enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(localDynamoClient)
                .build();

        upcomingGamesTable = spy(enhancedClient.table("UpcomingGames", upcomingGameTableSchema));

        EnhancedGlobalSecondaryIndex modelIndex = EnhancedGlobalSecondaryIndex.builder()
                .indexName("modelIdIndex")
                .projection(Projection.builder()
                        .projectionType(ProjectionType.ALL)
                        .build())
                .build();

        upcomingGamesTable.createTable(CreateTableEnhancedRequest.builder()
                .globalSecondaryIndices(modelIndex)
                .build());

        modelIdIndex = spy(upcomingGamesTable.index("modelIdIndex"));

        upcomingGamesDAO = new DDBUpcomingGamesDAO(enhancedClient, upcomingGamesTable, modelIdIndex);
    }

    @Test
    public void addUpcomingGameForModel_successTest() throws PersistenceException {
        upcomingGamesDAO.addUpcomingGamesForModel(MODEL_ID, GAME_ID, getUpcomingGameThatMeetsModel());

        DDBUpcomingGame upcomingGame = upcomingGamesTable.getItem(Key.builder()
                .partitionValue(GAME_ID)
                .sortValue(MODEL_ID)
                .build());

        assertThat(upcomingGame).isNotNull();
        assertThat(upcomingGame.getGameId()).isEqualTo(GAME_ID);
        assertThat(upcomingGame.getModelId()).isEqualTo(MODEL_ID);
    }

    @Test
    public void addUpcomingGameForModel_exceptionThrown() {
        doThrow(DynamoDbException.class).when(upcomingGamesTable).putItem(any(PutItemEnhancedRequest.class));
        assertThatThrownBy(() -> upcomingGamesDAO.addUpcomingGamesForModel(MODEL_ID, GAME_ID, getUpcomingGameThatMeetsModel()))
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    public void getUpcomingGamesForModel_successTest() throws PersistenceException {
        upcomingGamesDAO.addUpcomingGamesForModel(MODEL_ID, GAME_ID, getUpcomingGameThatMeetsModel());

        List<UpcomingGameThatMeetsModel> upcomingGames = upcomingGamesDAO.getUpcomingGamesForModel(MODEL_ID);
        assertThat(upcomingGames.size()).isEqualTo(1);
        assertThat(upcomingGames.get(0).getGameId()).isEqualTo(GAME_ID);
    }

    @Test
    public void getUpcomingGamesForModel_exceptionThrown() throws PersistenceException {
        upcomingGamesDAO.addUpcomingGamesForModel(MODEL_ID, GAME_ID, getUpcomingGameThatMeetsModel());

        doThrow(DynamoDbException.class).when(modelIdIndex).query(any(QueryEnhancedRequest.class));

        assertThatThrownBy(() -> upcomingGamesDAO.getUpcomingGamesForModel(MODEL_ID))
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    public void getUpcomingGamesForModel_noneExist() throws PersistenceException {
        List<UpcomingGameThatMeetsModel> upcomingGames = upcomingGamesDAO.getUpcomingGamesForModel(MODEL_ID);
        assertThat(upcomingGames.size()).isEqualTo(0);
    }

    @Test
    public void deleteUpcomingGameForModel_success() throws PersistenceException {
        upcomingGamesDAO.addUpcomingGamesForModel(MODEL_ID, GAME_ID, getUpcomingGameThatMeetsModel());

        DDBUpcomingGame upcomingGame = upcomingGamesTable.getItem(Key.builder()
                .partitionValue(GAME_ID)
                .sortValue(MODEL_ID)
                .build());

        assertThat(upcomingGame).isNotNull();

        upcomingGamesDAO.deleteUpcomingGameForModel(MODEL_ID, GAME_ID);

        upcomingGame = upcomingGamesTable.getItem(Key.builder()
                .partitionValue(GAME_ID)
                .sortValue(MODEL_ID)
                .build());

        assertThat(upcomingGame).isNull();
    }

    @Test
    public void deleteUpcomingGameForModel_exceptionThrown() throws PersistenceException {
        upcomingGamesDAO.addUpcomingGamesForModel(MODEL_ID, GAME_ID, getUpcomingGameThatMeetsModel());

        DDBUpcomingGame upcomingGame = upcomingGamesTable.getItem(Key.builder()
                .partitionValue(GAME_ID)
                .sortValue(MODEL_ID)
                .build());

        assertThat(upcomingGame).isNotNull();

        doThrow(DynamoDbException.class).when(upcomingGamesTable).deleteItem(any(Key.class));

        assertThatThrownBy(() -> upcomingGamesDAO.deleteUpcomingGameForModel(MODEL_ID, GAME_ID))
                .isInstanceOf(PersistenceException.class);

        upcomingGame = upcomingGamesTable.getItem(Key.builder()
                .partitionValue(GAME_ID)
                .sortValue(MODEL_ID)
                .build());

        assertThat(upcomingGame).isNotNull();
    }

    @Test
    public void deleteAllUpcomingGamesForModel_successTest() throws PersistenceException, InterruptedException {
        upcomingGamesDAO.addUpcomingGamesForModel(MODEL_ID, GAME_ID, getUpcomingGameThatMeetsModel());
        upcomingGamesDAO.addUpcomingGamesForModel(MODEL_ID, "gameId2", UpcomingGameThatMeetsModel.builder()
                .gameId("gameId2")
                .build());

        assertThat(upcomingGamesDAO.getUpcomingGamesForModel(MODEL_ID).size()).isEqualTo(2);
        upcomingGamesDAO.deleteAllUpcomingGamesForModel(MODEL_ID);
        assertThat(upcomingGamesDAO.getUpcomingGamesForModel(MODEL_ID).size()).isEqualTo(0);
    }

    @Test
    public void deleteAllUpcomingGamesForModel_exceptionThrown() throws PersistenceException {
        DDBUpcomingGamesDAO mockedDAO = new DDBUpcomingGamesDAO(mockedEnhancedClient, upcomingGamesTable, modelIdIndex);

        mockedDAO.addUpcomingGamesForModel(MODEL_ID, GAME_ID, getUpcomingGameThatMeetsModel());

        doThrow(DynamoDbException.class).when(mockedEnhancedClient).batchWriteItem(any(BatchWriteItemEnhancedRequest.class));

        assertThatThrownBy(() -> mockedDAO.deleteAllUpcomingGamesForModel(MODEL_ID))
                .isInstanceOf(PersistenceException.class);
    }

    private UpcomingGameThatMeetsModel getUpcomingGameThatMeetsModel() {
        return UpcomingGameThatMeetsModel.builder()
                .gameId(GAME_ID)
                .build();
    }
}
