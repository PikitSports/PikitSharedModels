package com.pikit.shared.dagger;

import com.pikit.shared.dao.ddb.DDBGamesThatMeetModelDAO;
import com.pikit.shared.dao.ddb.DDBModelDAO;
import com.pikit.shared.dao.ddb.DDBUpcomingGamesDAO;
import com.pikit.shared.dao.ddb.model.DDBGamesThatMeetModel;
import com.pikit.shared.dao.ddb.model.DDBModel;
import com.pikit.shared.dao.ddb.model.DDBUpcomingGame;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Module
public class DAOModule {
    private static final String MODELS_TABLE_NAME = "Models";
    private static final String GAMES_THAT_MEET_MODEL_TABLE_NAME = "GamesThatMeetModel";
    private static final String UPCOMING_GAMES_TABLE_NAME = "UpcomingGamesForModel";
    private static final String MODEL_ID_INDEX = "modelIdIndex";

    @Provides
    @Reusable
    DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.create();
    }

    @Provides
    @Reusable
    static DDBModelDAO modelDAO(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        DynamoDbTable<DDBModel> modelsTable = dynamoDbEnhancedClient.table(MODELS_TABLE_NAME, TableSchema.fromBean(DDBModel.class));
        return new DDBModelDAO(modelsTable);
    }

    @Provides
    @Reusable
    static DDBGamesThatMeetModelDAO gamesThatMeetModelDAO(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        DynamoDbTable<DDBGamesThatMeetModel> gamesThatMeetModelTable =
                dynamoDbEnhancedClient.table(GAMES_THAT_MEET_MODEL_TABLE_NAME, TableSchema.fromBean(DDBGamesThatMeetModel.class));

        DynamoDbIndex<DDBGamesThatMeetModel> modelIdIndex = gamesThatMeetModelTable.index(MODEL_ID_INDEX);
        return new DDBGamesThatMeetModelDAO(dynamoDbEnhancedClient, gamesThatMeetModelTable, modelIdIndex);
    }

    @Provides
    @Reusable
    static DDBUpcomingGamesDAO upcomingGamesDAO(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        DynamoDbTable<DDBUpcomingGame> upcomingGamesTable = dynamoDbEnhancedClient.table(UPCOMING_GAMES_TABLE_NAME, TableSchema.fromBean(DDBUpcomingGame.class));
        DynamoDbIndex<DDBUpcomingGame> modelIdIndex = upcomingGamesTable.index(MODEL_ID_INDEX);

        return new DDBUpcomingGamesDAO(dynamoDbEnhancedClient, upcomingGamesTable, modelIdIndex);
    }
}
