package com.pikit.shared.dagger;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pikit.shared.client.S3Client;
import com.pikit.shared.dao.DataSourceDAO;
import com.pikit.shared.dao.GamesThatMeetModelDAO;
import com.pikit.shared.dao.ModelDAO;
import com.pikit.shared.dao.UpcomingGamesDAO;
import com.pikit.shared.dao.ddb.DDBModelDAO;
import com.pikit.shared.dao.ddb.DDBUpcomingGamesDAO;
import com.pikit.shared.dao.ddb.model.DDBModel;
import com.pikit.shared.dao.ddb.model.DDBUpcomingGame;
import com.pikit.shared.dao.s3.S3DataSourceDAO;
import com.pikit.shared.dao.s3.S3GamesThatMeetModelDAO;
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
    private static final String UPCOMING_GAMES_TABLE_NAME = "UpcomingGamesForModel";
    private static final String MODEL_ID_INDEX = "modelIdIndex";
    private static final String USER_MODELS_INDEX = "userModelsIndex";
    private static final String GAMES_BUCKET_NAME_KEY = "SLICK_PICK_GAMES_BUCKET";
    private static final String GAMES_THAT_MEET_MODEL_BUCKET_NAME_KEY = "GAMES_THAT_MEET_MODEL_BUCKET_NAME";

    @Provides
    @Reusable
    DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.create();
    }

    @Provides
    @Reusable
    static ModelDAO modelDAO(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        DynamoDbTable<DDBModel> modelsTable = dynamoDbEnhancedClient.table(MODELS_TABLE_NAME, TableSchema.fromBean(DDBModel.class));
        DynamoDbIndex<DDBModel> userModelsIndex = modelsTable.index(USER_MODELS_INDEX);
        return new DDBModelDAO(modelsTable, userModelsIndex);
    }

    @Provides
    @Reusable
    static GamesThatMeetModelDAO gamesThatMeetModelDAO(S3Client s3Client) {
        String bucketName = System.getenv(GAMES_THAT_MEET_MODEL_BUCKET_NAME_KEY);
        return new S3GamesThatMeetModelDAO(s3Client, bucketName);
    }

    @Provides
    @Reusable
    static UpcomingGamesDAO upcomingGamesDAO(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        DynamoDbTable<DDBUpcomingGame> upcomingGamesTable = dynamoDbEnhancedClient.table(UPCOMING_GAMES_TABLE_NAME, TableSchema.fromBean(DDBUpcomingGame.class));
        DynamoDbIndex<DDBUpcomingGame> modelIdIndex = upcomingGamesTable.index(MODEL_ID_INDEX);

        return new DDBUpcomingGamesDAO(dynamoDbEnhancedClient, upcomingGamesTable, modelIdIndex);
    }

    @Provides
    @Reusable
    static DataSourceDAO dataSourceDAO(AmazonS3 amazonS3, ObjectMapper objectMapper) {
        String gamesBucketName = System.getenv(GAMES_BUCKET_NAME_KEY);
        return new S3DataSourceDAO(amazonS3, gamesBucketName, objectMapper);
    }
}
