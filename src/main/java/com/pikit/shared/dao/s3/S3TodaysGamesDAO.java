package com.pikit.shared.dao.s3;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pikit.shared.client.S3Client;
import com.pikit.shared.dao.TodaysGamesDAO;
import com.pikit.shared.enums.League;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.RecentGame;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class S3TodaysGamesDAO implements TodaysGamesDAO {
    private static final String CURRENT_SCORES_KEY = "%s/currentScores/currentScores.json";
    private final S3Client s3Client;
    private final String bucketName;

    public S3TodaysGamesDAO(S3Client s3Client, String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public void saveTodaysGamesForLeague(League league, List<RecentGame> todaysGames) throws PersistenceException {
        try {
            s3Client.writeObjectToS3(bucketName, getS3Key(league), todaysGames, false);
        } catch (Exception e) {
            log.error("[S3] Exception thrown saving todays games for {}", league, e);
            throw new PersistenceException("Failed to save todays games");
        }
    }

    @Override
    public List<RecentGame> getTodaysGamesForLeague(League league) throws PersistenceException {
        try {
            return s3Client.getTypeReferenceFromS3(bucketName, getS3Key(league), new TypeReference<>(){}, false);
        } catch (Exception e) {
            log.error("[S3] Exception thrown getting todays games for {}", league, e);
            throw new PersistenceException("Failed to get todays games");
        }
    }

    private String getS3Key(League league) {
        return String.format(CURRENT_SCORES_KEY, league);
    }
}
