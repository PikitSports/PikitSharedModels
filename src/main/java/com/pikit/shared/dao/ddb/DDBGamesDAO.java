package com.pikit.shared.dao.ddb;

import com.pikit.shared.dao.GamesDAO;
import com.pikit.shared.dao.ddb.model.DDBGame;
import com.pikit.shared.dao.ddb.model.GameStatus;
import com.pikit.shared.enums.League;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.Game;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class DDBGamesDAO implements GamesDAO {
    private final DynamoDbTable<DDBGame> gamesTable;
    private final DynamoDbIndex<DDBGame> gameStatusIndex;
    private final DynamoDbIndex<DDBGame> gameDateIndex;

    public DDBGamesDAO(DynamoDbTable<DDBGame> gamesTable,
                       DynamoDbIndex<DDBGame> gameStatusIndex,
                       DynamoDbIndex<DDBGame> gameDateIndex) {
        this.gamesTable = gamesTable;
        this.gameStatusIndex = gameStatusIndex;
        this.gameDateIndex = gameDateIndex;
    }

    @Override
    public void saveGame(League league, Game game) throws PersistenceException {
        DDBGame ddbGame = game.toDDBGame(league);

        try {
            gamesTable.putItem(PutItemEnhancedRequest.builder(DDBGame.class)
                    .item(ddbGame)
                    .build());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception saving game {}:{}", league, game.gameId(), e);
            throw new PersistenceException("Failed to save game");
        }
    }

    @Override
    public Optional<Game> getGameFromId(League league, String gameId) throws PersistenceException {
        try {
            DDBGame game = gamesTable.getItem(Key.builder()
                    .partitionValue(gameId)
                    .sortValue(league.toString())
                    .build());

            if (game != null) {
                return Optional.of(game.toGame());
            } else {
                return Optional.empty();
            }
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown getting game {}:{}", league, gameId, e);
            throw new PersistenceException("Failed to get game");
        }
    }

    @Override
    public List<Game> getGamesForLeagueAndStatus(League league, GameStatus gameStatus) throws PersistenceException {
        try {
            QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .partitionValue(league.toString())
                            .sortValue(gameStatus.toString())
                            .build()))
                    .build();

            return gameStatusIndex.query(request)
                    .stream()
                    .flatMap(page -> page.items().stream())
                    .map(DDBGame::toGame)
                    .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown getting games from league {} and status {}", league, gameStatus, e);
            throw new PersistenceException("Failed to get games from league");
        }
    }

    @Override
    public void updateGameStatus(League league, Game game, GameStatus gameStatus) throws PersistenceException {
        DDBGame ddbGame = game.toDDBGame(league);
        ddbGame.setGameStatus(gameStatus);

        try {
            UpdateItemEnhancedRequest<DDBGame> request = UpdateItemEnhancedRequest.builder(DDBGame.class)
                    .item(ddbGame)
                    .ignoreNulls(true)
                    .build();

            gamesTable.updateItem(request);
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown updating game status {}:{}", league, game.gameId());
            throw new PersistenceException("Failed to update game status");
        }
    }

    @Override
    public List<Game> getGamesForLeagueAndDate(League league, String gameDate) throws PersistenceException {
        try {
            QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .partitionValue(league.toString())
                            .sortValue(gameDate)
                            .build()))
                    .build();

            return gameDateIndex.query(request)
                    .stream()
                    .flatMap(page -> page.items().stream())
                    .map(DDBGame::toGame)
                    .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            log.error("[DynamoDB] Exception thrown getting games from league {} and date {}", league, gameDate, e);
            throw new PersistenceException("Failed to get games from league for date");
        }
    }
}
