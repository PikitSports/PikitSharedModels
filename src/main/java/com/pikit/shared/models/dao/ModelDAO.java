package com.pikit.shared.models.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pikit.shared.models.ModelPerformance;
import com.pikit.shared.models.TopGameData;
import com.pikit.shared.models.ddb.DDBModel;

import java.util.Iterator;
import java.util.List;

public class ModelDAO {

    static AmazonDynamoDB dbClient = AmazonDynamoDBClientBuilder.defaultClient();
    static DynamoDB dynamoDB = new DynamoDB(dbClient);
    private static final String MODELS_TABLE_NAME = "Pikit_Models";
    static Table modelsTable = dynamoDB.getTable(MODELS_TABLE_NAME);
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static DDBModel getModelFromDynamo(String modelId) {
        try {
            QuerySpec spec = new QuerySpec().withKeyConditionExpression("modelId = :v_modelId")
                    .withValueMap(new ValueMap().withString(":v_modelId", modelId));

            ItemCollection<QueryOutcome> items = modelsTable.query(spec);
            Iterator<Item> iter = items.iterator();
            DDBModel model = null;
            while (iter.hasNext()) {
                String jsonValue = iter.next().toJSON();
                System.out.println("json value: " + jsonValue);
                model = objectMapper.readValue(jsonValue, DDBModel.class);
            }
            return model;
        } catch (Exception e) {
            System.out.println("Exception retrieving model from table: " + modelId);
            return null;
        }
    }

    public static void updateModelAfterModelRun(String modelId,
                                   ModelPerformance modelPerformance,
                                   List<TopGameData> top3Games,
                                   List<String> gamesToStore) {
        try {
            String updateExpression = "set modelPerformance = :v_performance,top3Games = :v_top3Games,gamesStored = :v_gamesStored";

            UpdateItemSpec updateSpec = new UpdateItemSpec().withPrimaryKey("modelId", modelId)
                    .withUpdateExpression(updateExpression)
                    .withValueMap(new ValueMap()
                            .withString(":v_performance", objectMapper.writeValueAsString(modelPerformance))
                            .withString(":v_top3Games", objectMapper.writeValueAsString(top3Games))
                            .withList(":v_gamesStored", gamesToStore));

            modelsTable.updateItem(updateSpec);
        } catch (Exception e) {
            System.out.println("Exception thrown updating model after model run: " + modelId);
        }
    }
}
