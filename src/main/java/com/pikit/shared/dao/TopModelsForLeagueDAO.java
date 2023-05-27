package com.pikit.shared.dao;

import com.pikit.shared.dao.s3.model.LastGamesIndex;
import com.pikit.shared.dao.ddb.model.DDBModel;
import com.pikit.shared.enums.League;
import com.pikit.shared.exceptions.PersistenceException;

import java.util.List;

public interface TopModelsForLeagueDAO {

    /**
     * Given a league and an index to retrieve from, retrieve the top games from this index.
     * @param league League to get the top models for
     * @param lastGamesIndex Index to retrieve the top models from
     * @return List of top models for this index in this league.
     * @throws PersistenceException When we are unable to retrieve the top models.
     */
    List<DDBModel> getTopModelsForLeague(League league, LastGamesIndex lastGamesIndex) throws PersistenceException;

    /**
     * Given a league, index, and list of top models, persist this information.
     * @param league League the top models are for
     * @param lastGamesIndex Index the models are the top of
     * @param topModels List of models
     * @throws PersistenceException When we are unable to persist the top models for league.
     */
    void setTopModelsForLeague(League league, LastGamesIndex lastGamesIndex, List<DDBModel> topModels) throws PersistenceException;
}
