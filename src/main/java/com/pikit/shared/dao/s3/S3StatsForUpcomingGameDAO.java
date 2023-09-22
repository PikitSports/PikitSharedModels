package com.pikit.shared.dao.s3;

import com.pikit.shared.client.S3Client;
import com.pikit.shared.dao.StatsForUpcomingGamesDAO;
import com.pikit.shared.enums.League;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.StatsForUpcomingGame;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class S3StatsForUpcomingGameDAO implements StatsForUpcomingGamesDAO {
    private static final String STATS_FOR_GAME_KEY = "%s/statsForUpcomingGames/%s.json";
    private static final String STATS_FOR_LEAGUE_KEY = "%s/statsForUpcomingGames";
    private final S3Client s3Client;
    private final String bucketName;

    public S3StatsForUpcomingGameDAO(S3Client s3Client, String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public void saveStatsForUpcomingGame(League league, String gameId, StatsForUpcomingGame statsForUpcomingGame) throws PersistenceException {
        try {
            s3Client.writeObjectToS3(bucketName, getGameStatsS3Key(league, gameId), statsForUpcomingGame, false);
        } catch (Exception e) {
            log.error("[S3] Exception thrown saving stats for upcoming game {}:{}", league, gameId, e);
            throw new PersistenceException("Failed to save stats for upcoming game");
        }
    }

    @Override
    public List<StatsForUpcomingGame> getStatsForUpcomingGames(League league) throws PersistenceException {
        try {
            return s3Client.getListOfObjectsFromS3(bucketName, getLeagueStatsS3Key(league), StatsForUpcomingGame.class, false);
        } catch (Exception e) {
            log.error("[S3] Exception thrown getting stats for upcoming games in league {}", league, e);
            throw new PersistenceException("Failed to get stats for upcoming games for league");
        }
    }

    private String getGameStatsS3Key(League league, String gameId) {
        return String.format(STATS_FOR_GAME_KEY, league, gameId);
    }

    private String getLeagueStatsS3Key(League league) {
        return String.format(STATS_FOR_LEAGUE_KEY, league);
    }
}
