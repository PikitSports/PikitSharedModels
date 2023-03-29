package com.pikit.shared.dao;

import com.pikit.shared.enums.League;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.Game;
import com.pikit.shared.models.StatMetadata;

import java.time.Period;
import java.util.List;
import java.util.Map;

public interface DataSourceDAO {

    /**
     * Given a league and a season, provide the list of games stored
     * @param league League to get games for
     * @param season Season to get games for
     * @return List of games for the season
     * @throws PersistenceException
     */
    List<Game> getGamesForSeason(League league, String season) throws PersistenceException;

    /**
     * Given a league, a specific season, and a query string, provide the list of games stored.
     * @param league League to get games for
     * @param season Season to get games for
     * @param queryString Query string for which fields to grab for each game
     * @return List of games containing the desired fields for each game
     * @throws PersistenceException
     */
    List<Game> queryGamesForSeason(League league, String season, String queryString) throws PersistenceException;

    /**
     * Given a league, a season, and a list of games to add, add the list of games stored for the season
     * @param league League to add games for
     * @param season Season to add games for
     * @param games List of games to add
     * @throws PersistenceException
     */
    void saveGamesForSeason(League league, String season, List<Game> games) throws PersistenceException;

    Map<String, StatMetadata> getStatsAvailableForLeague(League league) throws PersistenceException;
}
