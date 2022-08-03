package com.pikit.shared.models.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.pikit.shared.dagger.DaggerServiceComponent;
import com.pikit.shared.dagger.ServiceComponent;
import com.pikit.shared.exceptions.InternalErrorException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UpcomingGamesDAO {

    private static ServiceComponent SERVICE_COMPONENT = DaggerServiceComponent.create();
    private static Table upcomingGamesTable;
    private static Index modelIdIndex;

    public UpcomingGamesDAO() { this(SERVICE_COMPONENT); }

    public UpcomingGamesDAO(ServiceComponent serviceComponent) {
        upcomingGamesTable = serviceComponent.getUpcomingGamesTable();
        modelIdIndex = serviceComponent.getModelIdIndex();
    }

    public List<Map<String, Object>> getUpcomingGamesForModel(String modelId) throws InternalErrorException {
        try {
            List<Map<String, Object>> upcomingGamesForModel = new ArrayList<>();
            ItemCollection<QueryOutcome> response = modelIdIndex.query(new QuerySpec()
                    .withKeyConditionExpression("modelId = :v_modelId")
                    .withValueMap(new ValueMap()
                            .withString(":v_modelId", modelId)));

            Iterator<Item> iterator = response.iterator();
            Item game = null;
            while (iterator.hasNext()) {
                game = iterator.next();
                Map<String, Object> gameObject = game.asMap();
                upcomingGamesForModel.add(gameObject);
            }

            return upcomingGamesForModel;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception getting upcoming games for model: " + e);
            throw new InternalErrorException("Exception getting upcoming games for model: " + modelId);
        }
    }

    public void deleteUpcomingGameForModel(String modelId, String gameId) throws InternalErrorException {
        try {
            upcomingGamesTable.deleteItem(new PrimaryKey("gameId", gameId, "modelId", modelId));
        } catch (Exception e) {
            System.out.println("Exception thrown deleting upcoming game mapping: " + e);
            throw new InternalErrorException("Exception thrown deleting upcoming game mapping.");
        }
    }
}
