package com.pikit.shared.dao;

import com.pikit.shared.enums.League;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.StatsForUpcomingGame;

import java.util.List;

public interface StatsForUpcomingGamesDAO {

    /**
     * Given a game and stats for the game, persist the stats for the upcoming game.
     * @param league League of the game
     * @param gameId ID of game to save stats for
     * @param statsForUpcomingGame List of stats for the upcoming game
     * @throws PersistenceException when we are unable to save stats
     */
    void saveStatsForUpcomingGame(League league, String gameId, StatsForUpcomingGame statsForUpcomingGame) throws PersistenceException;

    /**
     * Given a league, retrieve the stats for the upcoming games for this league
     * @param league League of the game.
     * @return List of the stats for the upcoming games for this league
     * @throws PersistenceException when we are unable to retrieve stats for the games for this league
     */
    List<StatsForUpcomingGame> getStatsForUpcomingGames(League league) throws PersistenceException;
}
