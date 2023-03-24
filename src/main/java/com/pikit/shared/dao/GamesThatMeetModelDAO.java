package com.pikit.shared.dao;

import com.pikit.shared.dao.ddb.model.DDBGamesThatMeetModel;
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
     * Given a modelId and a Season, delete all games that met model during that season.
     * @param modelId ID of the model to delete games for
     * @param season Season that we should delete games for.
     */
    void deleteOldGamesThatMetModel(String modelId, String season) throws PersistenceException;

    /**
     * Given a modelId, retrieve all games that meet model
     * @param modelId ID of the model to retrieve games for
     * @return List of games that meet model
     * @throws PersistenceException
     */
    List<DDBGamesThatMeetModel> getGamesThatMeetModel(String modelId) throws PersistenceException;
}
