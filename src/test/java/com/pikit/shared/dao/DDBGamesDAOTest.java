package com.pikit.shared.dao;

import com.pikit.shared.dao.ddb.DDBGamesDAO;
import com.pikit.shared.dao.ddb.model.DDBGame;
import com.pikit.shared.dao.ddb.model.DDBModel;
import com.pikit.shared.dao.ddb.model.GameStatus;
import com.pikit.shared.dynamodb.LocalDynamoDB;
import com.pikit.shared.enums.League;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.BettingStats;
import com.pikit.shared.models.Game;
import com.pikit.shared.models.GameStats;
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

import static org.mockito.Mockito.spy;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DDBGamesDAOTest {
    private static final String HOME_TEAM = "team1";
    private static final String AWAY_TEAM = "team2";
    private static final String TEAM_3 = "team3";
    private static final int NUM_GAME_FOR_DAY = 0;
    private static final String GAME_DATE = "01/02/2023";
    private static final String EXPECTED_GAME_ID = "team1|team2|01/02/2023|0";
    private LocalDynamoDB localDynamoDB = new LocalDynamoDB();
    private DynamoDbTable<DDBGame> gamesTable;
    private DDBGamesDAO gamesDAO;
    private DynamoDbIndex<DDBGame> gameStatusTableIndex;

    @BeforeEach
    public void setup() {
        localDynamoDB.start();

        TableSchema<DDBGame> gameTableSchema = TableSchema.fromBean(DDBGame.class);

        DynamoDbClient localDynamoClient = localDynamoDB.createClient();
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(localDynamoClient)
                .build();

        gamesTable = spy(enhancedClient.table("Games", gameTableSchema));

        EnhancedGlobalSecondaryIndex gameStatusIndex = EnhancedGlobalSecondaryIndex.builder()
                .indexName("gameStatusIndex")
                .projection(Projection.builder()
                        .projectionType(ProjectionType.ALL)
                        .build())
                .build();

        gamesTable.createTable(CreateTableEnhancedRequest.builder()
                .globalSecondaryIndices(gameStatusIndex)
                .build());

        gameStatusTableIndex = spy(gamesTable.index("gameStatusIndex"));

        gamesDAO = new DDBGamesDAO(gamesTable, gameStatusTableIndex);
    }

    @Test
    public void saveGame_successTest() throws PersistenceException {
        assertThat(gamesTable.getItem(Key.builder()
                .partitionValue(EXPECTED_GAME_ID)
                .sortValue(League.NFL.toString())
                .build())).isNull();

        gamesDAO.saveGame(League.NFL, getGame());

        DDBGame game = gamesTable.getItem(Key.builder()
                .partitionValue(EXPECTED_GAME_ID)
                .sortValue(League.NFL.toString())
                .build());

        assertThat(game).isNotNull();
        assertThat(game.getGameId()).isEqualTo(EXPECTED_GAME_ID);
        assertThat(game.toGame().homeTeam()).isEqualTo(HOME_TEAM);
        assertThat(game.toGame().awayTeam()).isEqualTo(AWAY_TEAM);
    }

    @Test
    public void saveGame_exceptionThrown() {
        doThrow(DynamoDbException.class).when(gamesTable).putItem(any(PutItemEnhancedRequest.class));

        assertThatThrownBy(() -> gamesDAO.saveGame(League.NFL, getGame()))
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    public void getGameFromId_successTest() throws PersistenceException {
        Optional<Game> optionalGame = gamesDAO.getGameFromId(League.NFL, EXPECTED_GAME_ID);
        assertThat(optionalGame.isPresent()).isFalse();
        gamesDAO.saveGame(League.NFL, getGame());
        optionalGame = gamesDAO.getGameFromId(League.NFL, EXPECTED_GAME_ID);
        assertThat(optionalGame.isPresent()).isTrue();
    }

    @Test
    public void getGameFromId_notFound() throws PersistenceException {
        Optional<Game> optionalGame = gamesDAO.getGameFromId(League.NFL, EXPECTED_GAME_ID);
        assertThat(optionalGame.isPresent()).isFalse();
    }

    @Test
    public void getGameFromId_exceptionThrown() {
        doThrow(DynamoDbException.class).when(gamesTable).getItem(any(Key.class));
        assertThatThrownBy(() -> gamesDAO.getGameFromId(League.NFL, EXPECTED_GAME_ID))
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    public void getGamesFromLeagueAndStatus_successTest() throws PersistenceException {
        Game game1 = getGame();
        game1.setGameStatus(GameStatus.COMPLETED);

        GameStats gameStats = GameStats.builder()
                .homeTeam(TEAM_3)
                .awayTeam(AWAY_TEAM)
                .gameDate(GAME_DATE)
                .numGameForDay(NUM_GAME_FOR_DAY)
                .build();
        BettingStats bettingStats = BettingStats.builder().build();

        Game game2 = Game.fromGameAndBettingStats(gameStats, bettingStats);
        game2.setGameStatus(GameStatus.IN_PROGRESS);

        gamesDAO.saveGame(League.NFL, game1);
        gamesDAO.saveGame(League.NFL, game2);

        List<Game> inProgressGames = gamesDAO.getGamesForLeagueAndStatus(League.NFL, GameStatus.IN_PROGRESS);
        assertThat(inProgressGames.size()).isEqualTo(1);

        List<Game> completedGames = gamesDAO.getGamesForLeagueAndStatus(League.NFL, GameStatus.COMPLETED);
        assertThat(completedGames.size()).isEqualTo(1);

        List<Game> scheduledGames = gamesDAO.getGamesForLeagueAndStatus(League.NFL, GameStatus.SCHEDULED);
        assertThat(scheduledGames.size()).isEqualTo(0);
    }

    @Test
    public void getGamesFromLeagueAndStatus_exceptionThrown() {
        doThrow(DynamoDbException.class).when(gameStatusTableIndex).query(any(QueryEnhancedRequest.class));

        assertThatThrownBy(() -> gamesDAO.getGamesForLeagueAndStatus(League.NFL, GameStatus.COMPLETED))
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    public void updateGameStatus_successTest() throws PersistenceException {
        Game game = getGame();
        game.setGameStatus(GameStatus.IN_PROGRESS);

        gamesDAO.saveGame(League.MLB, game);

        DDBGame gameGet = gamesTable.getItem(Key.builder()
                .partitionValue(EXPECTED_GAME_ID)
                .sortValue(League.MLB.toString())
                .build());

        assertThat(gameGet.getGameStatus()).isEqualTo(GameStatus.IN_PROGRESS);

        gamesDAO.updateGameStatus(League.MLB, game, GameStatus.CANCELLED);

        gameGet = gamesTable.getItem(Key.builder()
                .partitionValue(EXPECTED_GAME_ID)
                .sortValue(League.MLB.toString())
                .build());

        assertThat(gameGet.getGameStatus()).isEqualTo(GameStatus.CANCELLED);
    }

    @Test
    public void updateGameStatus_exceptionThrown() {
        doThrow(DynamoDbException.class).when(gamesTable).updateItem(any(UpdateItemEnhancedRequest.class));

        assertThatThrownBy(() -> gamesDAO.updateGameStatus(League.MLB, getGame(), GameStatus.IN_PROGRESS))
                .isInstanceOf(PersistenceException.class);
    }

    private Game getGame() {
        GameStats gameStats = GameStats.builder()
                .homeTeam(HOME_TEAM)
                .awayTeam(AWAY_TEAM)
                .gameDate(GAME_DATE)
                .numGameForDay(NUM_GAME_FOR_DAY)
                .build();
        BettingStats bettingStats = BettingStats.builder().build();

        return Game.fromGameAndBettingStats(gameStats, bettingStats);
    }
}
