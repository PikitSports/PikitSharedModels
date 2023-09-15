package com.pikit.shared.dao;

import com.pikit.shared.exceptions.NotFoundException;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.GameThatMeetsModel;

import java.util.List;
import java.util.Map;

public interface GamesThatMeetModelDAO {
    /**
     * Given a modelId and games that meet model, store this data in the data store.
     * @param modelId ID of the model that the games are for.
     * @param gamesThatMeetModel A map of Seasons to List of Games.
     */
    void addGamesThatMeetModel(String modelId, Map<String, List<GameThatMeetsModel>> gamesThatMeetModel) throws PersistenceException;

    /**
     * Given a model, a season and the games that meet model for that season, store this data
     * @param modelId ID of the model that the games are for
     * @param season Season for the games
     * @param gamesThatMeetModel Games that met the model
     * @throws PersistenceException
     */
    void addGamesThatMeetModel(String modelId, String season, List<GameThatMeetsModel> gamesThatMeetModel) throws PersistenceException;

    /**
     * Given a modelId and a Season, delete all games that met model during that season.
     * @param modelId ID of the model to delete games for
     * @param season Season that we should delete games for.
     */
    void deleteOldGamesThatMetModel(String modelId, String season) throws PersistenceException;

    /**
     * Given a modelId and season, retrieve all games that meet model for that season
     * @param modelId ID of the model to retrieve games for
     * @param season Season to retrieve models for
     * @return List of games that meet model
     * @throws PersistenceException
     */
    List<GameThatMeetsModel> getGamesThatMeetModelForSeason(String modelId, String season) throws PersistenceException, NotFoundException;

    void deleteAllGamesThatMeetModel(String modelId) throws PersistenceException;

    /**
     * Given a modelId and a list of games that meet the model, persist these games.
     * @param modelId ID of the model to persist games for.
     * @param latestGames List of latest games that meet the model
     * @throws PersistenceException when we are unable to store the latest games for the model
     */
    void storeLatestGamesForModel(String modelId, List<GameThatMeetsModel> latestGames) throws PersistenceException;

    /**
     * Given a modelId, retrieve and return a list of the latest games that meet the model
     * @param modelId ID of the model to retrieve games for
     * @return List of latest games that meet the model
     * @throws PersistenceException when we are unable to retrieve the latest games for the model.
     */
    List<GameThatMeetsModel> getLatestGamesForModel(String modelId) throws PersistenceException, NotFoundException;
}
