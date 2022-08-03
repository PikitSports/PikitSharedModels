package com.pikit.shared.models.dao;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pikit.shared.dagger.DaggerServiceComponent;
import com.pikit.shared.dagger.ServiceComponent;
import com.pikit.shared.enums.League;
import com.pikit.shared.exceptions.InternalErrorException;
import com.pikit.shared.models.ModelConfiguration;
import com.pikit.shared.models.ModelPerformance;
import com.pikit.shared.models.TopGameData;
import com.pikit.shared.models.ddb.DDBModel;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class ModelDAO {

    private static ServiceComponent serviceComponent = DaggerServiceComponent.create();
    private static ObjectMapper objectMapper;
    private static Table modelsTable;

    private static final String USER_CREATED_BY = "userCreatedBy";
    private static final String MODEL_CONFIGURATION = "modelConfiguration";
    private static final String CREATION_TIMESTAMP = "creationTimestamp";
    private static final String GAMES_STORED = "gamesStored";
    private static final String MODEL_ID = "modelId";
    private static final String LEAGUE = "league";
    private static final String ALERTS_ENABLED = "alertsEnabled";

    public ModelDAO() { this(serviceComponent); }

    public ModelDAO(ServiceComponent serviceComponent) {
        objectMapper = serviceComponent.getObjectMapper();
        modelsTable = serviceComponent.getModelsTable();
    }

    public DDBModel getModelFromDynamo(String modelId) {
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

    public void updateModelAfterModelRun(String modelId,
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

    public String createModel(String userId,
                              ModelConfiguration modelConfiguration,
                              long creationTimestamp) {
        String modelId = UUID.randomUUID().toString().replace("-", "");
        try {
            Item item = new Item().withPrimaryKey(MODEL_ID, modelId)
                    .withString(USER_CREATED_BY, userId)
                    .withLong(CREATION_TIMESTAMP, creationTimestamp)
                    .withString(LEAGUE, modelConfiguration.getLeague().toString())
                    .withString(MODEL_CONFIGURATION, objectMapper.writeValueAsString(modelConfiguration))
                    .withBoolean(ALERTS_ENABLED, true);
            modelsTable.putItem(item);
            return modelId;
        } catch (Exception e) {
            System.out.println("Exception thrown creating model in ddb: " + modelId);
            return null;
        }
    }

    public void updateModel(String modelId, String userCreatedBy,
                            long creationTimestamp, League league,
                            List<String> gamesStored, ModelConfiguration modelConfiguration,
                            boolean alertsEnabled) throws InternalErrorException {
        try {
            Item item = new Item().withPrimaryKey(MODEL_ID, modelId)
                    .withString(USER_CREATED_BY, userCreatedBy)
                    .withLong(CREATION_TIMESTAMP, creationTimestamp)
                    .withString(LEAGUE, league.toString())
                    .withList(GAMES_STORED, gamesStored)
                    .withString(MODEL_CONFIGURATION, objectMapper.writeValueAsString(modelConfiguration))
                    .withBoolean(ALERTS_ENABLED, alertsEnabled);
            modelsTable.putItem(item);
        } catch (Exception e) {
            System.out.println("Exception thrown updating model in dynamo for model: " + modelId);
            throw new InternalErrorException("Exception thrown updating model in dynamo.");
        }
    }
}
