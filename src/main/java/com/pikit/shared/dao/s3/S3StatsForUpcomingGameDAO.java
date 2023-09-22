package com.pikit.shared.dao.s3;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pikit.shared.client.S3Client;
import com.pikit.shared.dao.StatsForUpcomingGamesDAO;
import com.pikit.shared.enums.League;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.StatsForUpcomingGame;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class S3StatsForUpcomingGameDAO implements StatsForUpcomingGamesDAO {
    private static final String STATS_FOR_LEAGUE_KEY = "%s/statsForUpcomingGames.json";
    private final S3Client s3Client;
    private final String bucketName;

    public S3StatsForUpcomingGameDAO(S3Client s3Client, String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public void saveStatsForUpcomingGames(League league, List<StatsForUpcomingGame> statsForUpcomingGames) throws PersistenceException {
        try {
            s3Client.writeObjectToS3(bucketName, getLeagueStatsS3Key(league), statsForUpcomingGames, false);
        } catch (Exception e) {
            log.error("[S3] Exception thrown saving stats for upcoming games for league {}", league, e);
            throw new PersistenceException("Failed to save stats for upcoming games");
        }
    }

    @Override
    public List<StatsForUpcomingGame> getStatsForUpcomingGames(League league) throws PersistenceException {
        try {
            return s3Client.getTypeReferenceFromS3(bucketName, getLeagueStatsS3Key(league), new TypeReference<>(){}, false);
        } catch (Exception e) {
            log.error("[S3] Exception thrown getting stats for upcoming games in league {}", league, e);
            throw new PersistenceException("Failed to get stats for upcoming games for league");
        }
    }

    private String getLeagueStatsS3Key(League league) {
        return String.format(STATS_FOR_LEAGUE_KEY, league);
    }
}
