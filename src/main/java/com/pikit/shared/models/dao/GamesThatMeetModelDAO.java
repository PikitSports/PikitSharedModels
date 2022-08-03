package com.pikit.shared.models.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pikit.shared.dagger.DaggerServiceComponent;
import com.pikit.shared.dagger.ServiceComponent;
import com.pikit.shared.models.GameThatMeetsModel;
import com.sun.jmx.remote.internal.ArrayQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GamesThatMeetModelDAO {

    private static Table gamesThatMeetModelTable;
    private static ObjectMapper objectMapper;

    private static ServiceComponent serviceComponent = DaggerServiceComponent.create();

    public GamesThatMeetModelDAO() { this(serviceComponent); }

    public GamesThatMeetModelDAO(ServiceComponent serviceComponent) {
        gamesThatMeetModelTable = serviceComponent.getGamesThatMeetModelTable();
        objectMapper = serviceComponent.getObjectMapper();
    }

    public List<String> addGamesThatMeetModel(String modelId, TreeMap<String, List<GameThatMeetsModel>> gamesThatMeetModel) {
        try {
            List<String> gamesToStore = new ArrayList<>();
            for (Map.Entry<String, List<GameThatMeetsModel>> entry: gamesThatMeetModel.entrySet()) {
                String season = entry.getKey();
                List<GameThatMeetsModel> gamesThatMeetModelList = entry.getValue();
                gamesToStore.add(season);
                String key = modelId + "|" + season;
                Item seasonToAdd = new Item().withPrimaryKey("id", key)
                        .withString("games", objectMapper.writeValueAsString(gamesThatMeetModelList));
                gamesThatMeetModelTable.putItem(seasonToAdd);
                System.out.println("Done adding games to the games that meet model table.");
            }
            return gamesToStore;
        } catch (Exception e) {
            System.out.println("Exception adding games that meet model: " + modelId);
            return null;
        }
    }

    public void deleteOldGamesThatMetModel(String modelId, String season) {
        try {
            String key = modelId + "|" + season;
            gamesThatMeetModelTable.deleteItem("id", key);
        } catch (Exception e) {
            System.out.println("Exception deleting previously saved games that meet model: " + modelId + ", season: " + season);
        }
    }
}
