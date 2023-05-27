package com.pikit.shared.dao.s3;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pikit.shared.client.S3Client;
import com.pikit.shared.dao.TopModelsForLeagueDAO;
import com.pikit.shared.dao.ddb.model.DDBModel;
import com.pikit.shared.dao.s3.model.LastGamesIndex;
import com.pikit.shared.enums.League;
import com.pikit.shared.exceptions.PersistenceException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;

@Slf4j
public class S3TopModelsForLeagueDAO implements TopModelsForLeagueDAO {
    private static final String TOP_MODELS_FOR_LEAGUE_PATH = "%s/topModelsForLeague/%s.json";
    //Temporary cache for lambda. This will help during the 15 minutes of execution time of the lambda so we don't have to call S3 for every request.
    //Especially since the data will only change once per day.
    private static final HashMap<String, List<DDBModel>> cachedTopModels = new HashMap<>();
    private final S3Client s3Client;
    private final String bucketName;

    public S3TopModelsForLeagueDAO(S3Client s3Client, String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public List<DDBModel> getTopModelsForLeague(League league, LastGamesIndex lastGamesIndex) throws PersistenceException {
        try {
            String key = String.format(TOP_MODELS_FOR_LEAGUE_PATH, league, lastGamesIndex);

            if (cachedTopModels.containsKey(key) && cachedTopModels.get(key) != null) {
                return cachedTopModels.get(key);
            }

            List<DDBModel> models = s3Client.getTypeReferenceFromS3(bucketName, key, new TypeReference<>(){}, false);
            cachedTopModels.put(key, models);

            return models;
        } catch (Exception e) {
            log.error("[S3] Exception thrown getting top models for league {}:{}", league, lastGamesIndex, e);
            throw new PersistenceException("Failed to get top models for league");
        }
    }

    @Override
    public void setTopModelsForLeague(League league, LastGamesIndex lastGamesIndex, List<DDBModel> topModels) throws PersistenceException {
        try {
            String key = String.format(TOP_MODELS_FOR_LEAGUE_PATH, league, lastGamesIndex);
            s3Client.writeObjectToS3(bucketName, key, topModels, false);
        } catch (Exception e) {
            log.error("[S3] Exception thrown setting top models for league {}:{}", league, lastGamesIndex, e);
            throw new PersistenceException("Failed to set top models for league");
        }
    }
}
