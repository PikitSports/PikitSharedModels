package com.pikit.shared.dao;

import com.pikit.shared.enums.League;
import com.pikit.shared.exceptions.NotFoundException;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.Game;

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
}
