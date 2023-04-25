package com.pikit.shared.dao.s3;

import com.amazonaws.SdkClientException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.pikit.shared.client.S3Client;
import com.pikit.shared.dao.GamesThatMeetModelDAO;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.GameThatMeetsModel;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
public class S3GamesThatMeetModelDAO implements GamesThatMeetModelDAO {
    private static final String GAMES_THAT_MEET_MODEL_KEY = "%s/%s.json";
    private final S3Client s3Client;
    private final String bucketName;

    public S3GamesThatMeetModelDAO(S3Client s3Client, String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public void addGamesThatMeetModel(String modelId, Map<String, List<GameThatMeetsModel>> gamesThatMeetModel) throws PersistenceException {
        for (Map.Entry<String, List<GameThatMeetsModel>> entry: gamesThatMeetModel.entrySet()) {
            addGamesThatMeetModel(modelId, entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void addGamesThatMeetModel(String modelId, String season, List<GameThatMeetsModel> gamesThatMeetModel) throws PersistenceException {
        try {
           s3Client.writeObjectToS3(bucketName, getS3Key(modelId, season), gamesThatMeetModel, false);
        } catch (SdkClientException | IOException e) {
            log.error("[S3] Exception thrown adding games that meet model {} for season {}", modelId, season, e);
            throw new PersistenceException("Failed to add games that meet model");
        }
    }

    @Override
    public void deleteOldGamesThatMetModel(String modelId, String season) throws PersistenceException {
        try {
            s3Client.deleteObjectFromS3(bucketName, getS3Key(modelId, season));
        } catch (SdkClientException e) {
            log.error("[S3] Exception thrown deleting games that meet model {} for season {}", modelId, season, e);
            throw new PersistenceException("Failed to delete games that meet model for season");
        }
    }

    @Override
    public List<GameThatMeetsModel> getGamesThatMeetModelForSeason(String modelId, String season) throws PersistenceException {
        try {
            return s3Client.getTypeReferenceFromS3(bucketName, getS3Key(modelId, season), new TypeReference<>(){}, false);
        } catch (SdkClientException | IOException e) {
            log.error("[S3] Exception thrown getting games that meet model {} for season {}", modelId, season, e);
            throw new PersistenceException("Failed to get games that meet model for season");
        }
    }

    private String getS3Key(String modelId, String season) {
        return String.format(GAMES_THAT_MEET_MODEL_KEY, modelId, season);
    }
}
