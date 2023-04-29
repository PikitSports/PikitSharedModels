package com.pikit.shared.dao;

import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.UpcomingGameThatMeetsModel;

import java.util.List;

public interface UpcomingGamesDAO {

    /**
     * Given a modelId and list of games, add the games to upcoming games for the model.
     * @param modelId ID of the model to update
     * @param gameId ID of the game to add for the model
     * @param upcomingGame Game information for the game to add
     * @throws PersistenceException
     */
    void addUpcomingGameForModel(String modelId, String gameId, UpcomingGameThatMeetsModel upcomingGame) throws PersistenceException;

    /**
     * Given a modelId, return all upcoming games that meet the model
     * @param modelId ID of the model to retrieve games for
     * @return all upcoming games for the model.
     * @throws PersistenceException
     */
    List<UpcomingGameThatMeetsModel> getUpcomingGamesForModel(String modelId) throws PersistenceException;

    /**
     * Given a model and a game, delete the upcoming game from the model.
     * @param modelId ID of the model
     * @param gameId ID of the game to delete from the model.
     * @throws PersistenceException
     */
    void deleteUpcomingGameForModel(String modelId, String gameId) throws PersistenceException;

    /**
     * Given a model, retrieve and delete all upcoming games for the model
     * @param modelId ID of the model to delete upcoming games for
     * @throws PersistenceException
     */
    void deleteAllUpcomingGamesForModel(String modelId) throws PersistenceException;
}
