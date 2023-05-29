package com.pikit.shared.dao;

import com.pikit.shared.dao.ddb.model.GameStatus;
import com.pikit.shared.enums.League;
import com.pikit.shared.exceptions.NotFoundException;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.Game;

import java.util.List;
import java.util.Optional;

public interface GamesDAO {

    /**
     * Given game object, persist the game.
     * @param league League the game is in
     * @param game Game object to save
     * @throws PersistenceException when we are unable to save the game object.
     */
    void saveGame(League league, Game game) throws PersistenceException;

    /**
     * Given gameId, retrieve the game and return it's object.
     * @param league League that the game is in.
     * @param gameId ID of the game to retrieve.
     * @return Game object
     * @throws PersistenceException when we are unable to retrieve the game
     */
    Optional<Game> getGameFromId(League league, String gameId) throws PersistenceException;

    /**
     * Given a league, retrieve the games for this league with the specified game status
     * @param league League to retrieve games for
     * @param gameStatus Game status filter for games to return.
     * @return List of games in specified league with specified game status
     * @throws PersistenceException When we are unable to retrieve the games.
     */
    List<Game> getGamesForLeagueAndStatus(League league, GameStatus gameStatus) throws PersistenceException;

    /**
     * Given a league and a game, update the game status to the desired game status
     * @param league League of the game
     * @param game Game object
     * @param gameStatus Game status to update the game to
     * @throws PersistenceException When we are unable to update the game status.
     */
    void updateGameStatus(League league, Game game, GameStatus gameStatus) throws PersistenceException;

    /**
     * Given a league and a date, retrieve the games by this game date.
     * @param league League to retrieve games for
     * @param gameDate Date to retrieve games for
     * @return List of games for this league and date
     * @throws PersistenceException when we are unable to retrieve list of games.
     */
    List<Game> getGamesForLeagueAndDate(League league, String gameDate) throws PersistenceException;
}
