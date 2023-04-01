package com.pikit.shared.dao.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.pikit.shared.dao.DataSourceDAO;
import com.pikit.shared.dao.s3.model.DataSourceConfig;
import com.pikit.shared.enums.League;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.Game;
import com.pikit.shared.models.StatMetadata;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Slf4j
public class S3DataSourceDAO implements DataSourceDAO {
    private static final String DATA_SOURCE_KEY = "%s/%s.json.gz";
    private static final String DATA_SOURCE_CONFIG_KEY = "%s/dataSourceConfig.json";
    private final AmazonS3 amazonS3;
    private final String bucketName;
    private final ObjectMapper objectMapper;

    public S3DataSourceDAO(AmazonS3 amazonS3, String bucketName, ObjectMapper objectMapper) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Game> getGamesForSeason(League league, String season) throws  PersistenceException {
        try {
            String dataSourceFile = String.format(DATA_SOURCE_KEY, league, season);
            S3Object object = amazonS3.getObject(bucketName, dataSourceFile);
            GZIPInputStream gzipInputStream = new GZIPInputStream(object.getObjectContent());
            List<Game> games = objectMapper.readValue(gzipInputStream, new TypeReference<List<Game>>(){});
            gzipInputStream.close();
            return games;
        } catch (Exception e) {
            log.error("[S3] Exception thrown getting {} games for season {}", league, season, e);
            throw new PersistenceException("Failed to get games for season");
        }
    }

    @Override
    public List<Game> queryGamesForSeason(League league, String season, String queryString) throws PersistenceException {
        List<Game> gamesForSeason = new ArrayList<>();
        String dataSourceFile = String.format(DATA_SOURCE_KEY, league, season);
        SelectObjectContentRequest selectRequest = new SelectObjectContentRequest()
                .withBucketName(bucketName)
                .withKey(dataSourceFile)
                .withExpression(queryString)
                .withExpressionType(ExpressionType.SQL);

        InputSerialization inputSerialization = new InputSerialization();
        inputSerialization.setCompressionType(CompressionType.GZIP);
        inputSerialization.setJson(new JSONInput().withType(JSONType.DOCUMENT));

        OutputSerialization outputSerialization = new OutputSerialization();
        outputSerialization.setJson(new JSONOutput().withRecordDelimiter(","));

        selectRequest.setInputSerialization(inputSerialization);
        selectRequest.setOutputSerialization(outputSerialization);

        try (SelectObjectContentResult result = amazonS3.selectObjectContent(selectRequest)) {
            try (InputStream resultInputStream = result.getPayload().getRecordsInputStream(
                    new SelectObjectContentEventVisitor() {
                        @Override
                        public void visit(SelectObjectContentEvent.StatsEvent event) {
                            System.out.println("Received stats, bytes scanned" + event.getDetails().getBytesScanned()
                                    + " bytes processed" + event.getDetails().getBytesProcessed());
                        }

                        @Override
                        public void visit(SelectObjectContentEvent.EndEvent event) {
                            System.out.println("Result is complete");
                        }
                    }
            )) {
                String records = new String(IOUtils.toByteArray(resultInputStream)).trim();
                //need to remove the last comma from records.
                records = records.substring(0, records.length() - 1);
                records = "[" + records + "]";
                ArrayList<HashMap<String, Object>> jsonArray = objectMapper.readValue(records, new TypeReference<ArrayList<HashMap<String, Object>>>(){});
                for (int i = 0; i < jsonArray.size(); i ++) {
                    HashMap<String, Object> object = jsonArray.get(i);
                    String startingJson = "{\"gameStats\": {}, \"bettingStats\": {}, \"homeTeamStats\":  {}, \"awayTeamStats\":  {}}";
                    DocumentContext jsonDoc = JsonPath.parse(startingJson);
                    for (Map.Entry<String, Object> entry: object.entrySet()) {
                        String jsonPath = entry.getKey();
                        Object value = entry.getValue();
                        if (jsonPath.contains("$.")) {
                            //$.bettingStats.overUnder ==> Path = $.bettingStats, New Key = overUnder
                            String[] split = jsonPath.split("\\.");
                            String path = "$." + split[1];
                            String key = split[2];
                            jsonDoc = jsonDoc.put(path, key, value);
                        } else {
                            //bettingStats => Path => $.
                            String path = "$";
                            jsonDoc = jsonDoc.put(path, jsonPath, value);
                        }
                    }

                    gamesForSeason.add(objectMapper.readValue(jsonDoc.jsonString(), Game.class));
                }

                return gamesForSeason;
            }
        } catch (Exception e) {
            log.error("[S3] Exception thrown querying {} games for season {}", league, season, e);
            throw new PersistenceException("Failed to query games for season");
        }
    }

    @Override
    public void saveGamesForSeason(League league, String season, List<Game> games) throws PersistenceException {
        try {
            String dataSourceFile = String.format(DATA_SOURCE_KEY, league, season);
            byte[] gamesBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(games);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gzipOutputStream.write(gamesBytes, 0, gamesBytes.length);
            gzipOutputStream.finish();
            gzipOutputStream.close();

            byte[] zippedGameBytes = byteArrayOutputStream.toByteArray();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(zippedGameBytes.length);
            amazonS3.putObject(new PutObjectRequest(bucketName,
                    dataSourceFile,
                    new ByteArrayInputStream(zippedGameBytes),
                    objectMetadata));
        } catch (Exception e) {
            log.error("[S3] Exception thrown saving {} games for season {}", league, season, e);
            throw new PersistenceException("Failed to save games for season");
        }
    }

    @Override
    public Map<String, StatMetadata> getStatsAvailableForLeague(League league) throws PersistenceException {
        try {
            String dataSourceConfigFile = String.format(DATA_SOURCE_CONFIG_KEY, league);
            S3Object object = amazonS3.getObject(bucketName, dataSourceConfigFile);
            DataSourceConfig dataSourceConfig = objectMapper.readValue(object.getObjectContent(), DataSourceConfig.class);
            return dataSourceConfig.getStatsAvailable();
        } catch (Exception e) {
            log.error("[S3] Exception thrown getting stats available for league {}", league, e);
            throw new PersistenceException("Failed to get stats available for league");
        }
    }
}
