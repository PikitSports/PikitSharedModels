package com.pikit.shared.datasource;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pikit.shared.dagger.DaggerServiceComponent;
import com.pikit.shared.dagger.ServiceComponent;
import com.pikit.shared.models.Game;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DataSourceUtil {

    private static ServiceComponent serviceComponent = DaggerServiceComponent.create();
    private static final String S3_BUCKET = "pikit-games";
    private static AmazonS3 s3Client;
    private static ObjectMapper objectMapper;

    public DataSourceUtil() { this(serviceComponent); }

    public DataSourceUtil(ServiceComponent serviceComponent) {
        s3Client = serviceComponent.getS3Client();
        objectMapper = serviceComponent.getObjectMapper();
    }

    public List<Game> getGamesFromDataSource(String leagueFile) {
        try {
            System.out.println("Attempting to get games from league file: " + leagueFile);
            S3Object object = s3Client.getObject(S3_BUCKET, leagueFile);
            GZIPInputStream gzipInputStream = new GZIPInputStream(object.getObjectContent());
            List<Game> games = objectMapper.readValue(gzipInputStream, new TypeReference<List<Game>>(){});
            System.out.println("Found " + games.size() + " games.");
            gzipInputStream.close();
            return games;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception de-compressing file.");
            return null;
        }
    }

    public void saveGamesForDataSource(String leagueFile, List<Game> games) {
        try {
            byte[] gamesBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(games);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gzipOutputStream.write(gamesBytes, 0, gamesBytes.length);
            gzipOutputStream.finish();
            gzipOutputStream.close();

            byte[] zippedGameBytes = byteArrayOutputStream.toByteArray();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(zippedGameBytes.length);
            s3Client.putObject(new PutObjectRequest(S3_BUCKET,
                    leagueFile,
                    new ByteArrayInputStream(zippedGameBytes),
                    objectMetadata));
            System.out.println("Successfully added games to file.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception thrown writing games file.");
        }
    }
}
