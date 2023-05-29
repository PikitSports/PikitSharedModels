package com.pikit.shared.dao;

import com.pikit.shared.enums.League;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.RecentGame;

import java.util.List;

public interface TodaysGamesDAO {

    /**
     * Given a league and list of recent games, store those games
     * @param league League for recent games
     * @param todaysGames List of recent games for today.
     * @throws PersistenceException when we are unable to save todays games
     */
    void saveTodaysGamesForLeague(League league, List<RecentGame> todaysGames) throws PersistenceException;

    /**
     * Given a league, retrieve the recent games for that league.
     * @param league League to retrieve games for
     * @return List of games from today
     * @throws PersistenceException When we are unable to retrieve todays games.
     */
    List<RecentGame> getTodaysGamesForLeague(League league) throws PersistenceException;
}
