package com.pikit.shared.dagger;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pikit.shared.client.S3Client;
import com.pikit.shared.dao.*;
import com.pikit.shared.dao.ddb.*;
import com.pikit.shared.dao.ddb.model.*;
import com.pikit.shared.dao.s3.*;
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
    private static final String UPCOMING_GAMES_TABLE_NAME = "UpcomingGamesThatMeetModel";
    private static final String MODEL_ID_INDEX = "modelIdIndex";
    private static final String USER_MODELS_INDEX = "userModelsIndex";
    private static final String LEAGUE_INDEX = "leagueIndex";
    private static final String GAMES_BUCKET_NAME_KEY = "SLICK_PICK_GAMES_BUCKET";
    private static final String GAMES_THAT_MEET_MODEL_BUCKET_NAME_KEY = "GAMES_THAT_MEET_MODEL_BUCKET_NAME";
    private static final String MODEL_SUBSCRIBERS_TABLE_NAME = "ModelSubscribers";
    private static final String MODEL_FOLLOWERS_TABLE_NAME = "ModelFollowers";
    private static final String USER_ID_INDEX = "userIdIndex";
    private static final String USERS_TABLE_NAME = "Users";
    private static final String GAMES_TABLE_NAME = "Games";
    private static final String LAST_10_GAMES_INDEX_NAME = "last10GamesIndex";
    private static final String LAST_50_GAMES_INDEX_NAME = "last50GamesIndex";
    private static final String LAST_100_GAMES_INDEX_NAME = "last100GamesIndex";
    private static final String GAME_STATUS_INDEX_NAME = "gameStatusIndex";
    private static final String GROUPS_TABLE_NAME = "Groups";
    private static final String USER_GROUPS_INDEX = "userGroupsIndex";

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
        DynamoDbIndex<DDBModel> leagueModelsIndex = modelsTable.index(LEAGUE_INDEX);
        DynamoDbIndex<DDBModel> last10GamesIndex = modelsTable.index(LAST_10_GAMES_INDEX_NAME);
        DynamoDbIndex<DDBModel> last50GamesIndex = modelsTable.index(LAST_50_GAMES_INDEX_NAME);
        DynamoDbIndex<DDBModel> last100GamesIndex = modelsTable.index(LAST_100_GAMES_INDEX_NAME);

        return new DDBModelDAO(modelsTable, userModelsIndex, leagueModelsIndex, last10GamesIndex, last50GamesIndex, last100GamesIndex);
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

    @Provides
    @Reusable
    static ModelSubscribersDAO modelSubscribersDAO(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        DynamoDbTable<DDBModelSubscribers> modelSubscribersTable = dynamoDbEnhancedClient.table(MODEL_SUBSCRIBERS_TABLE_NAME, TableSchema.fromBean(DDBModelSubscribers.class));
        DynamoDbIndex<DDBModelSubscribers> userIdIndex = modelSubscribersTable.index(USER_ID_INDEX);

        return new DDBModelSubscribersDAO(modelSubscribersTable, userIdIndex);
    }

    @Provides
    @Reusable
    static ModelFollowersDAO modelFollowersDAO(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        DynamoDbTable<DDBModelFollowers> modelFollowersTable = dynamoDbEnhancedClient.table(MODEL_FOLLOWERS_TABLE_NAME, TableSchema.fromBean(DDBModelFollowers.class));
        DynamoDbIndex<DDBModelFollowers> userIdIndex = modelFollowersTable.index(USER_ID_INDEX);

        return new DDBModelFollowersDAO(modelFollowersTable, userIdIndex);
    }

    @Provides
    @Reusable
    static UserDAO userDAO(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        DynamoDbTable<DDBUser> userTable = dynamoDbEnhancedClient.table(USERS_TABLE_NAME, TableSchema.fromBean(DDBUser.class));
        return new DDBUserDAO(userTable);
    }

    @Provides
    @Reusable
    static GamesDAO gamesDAO(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        DynamoDbTable<DDBGame> gamesTable = dynamoDbEnhancedClient.table(GAMES_TABLE_NAME, TableSchema.fromBean(DDBGame.class));
        DynamoDbIndex<DDBGame> gameStatusIndex = gamesTable.index(GAME_STATUS_INDEX_NAME);
        return new DDBGamesDAO(gamesTable, gameStatusIndex);
    }

    @Provides
    @Reusable
    static TopModelsForLeagueDAO topModelsForLeagueDAO(S3Client s3Client) {
        String gamesBucketName = System.getenv(GAMES_BUCKET_NAME_KEY);
        return new S3TopModelsForLeagueDAO(s3Client, gamesBucketName);
    }

    @Provides
    @Reusable
    static TodaysGamesDAO todaysGamesDAO(S3Client s3Client) {
        String gamesBucketName = System.getenv(GAMES_BUCKET_NAME_KEY);
        return new S3TodaysGamesDAO(s3Client, gamesBucketName);
    }

    @Provides
    @Reusable
    static GroupDAO groupDAO(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        DynamoDbTable<DDBGroup> groupsTable = dynamoDbEnhancedClient.table(GROUPS_TABLE_NAME, TableSchema.fromBean(DDBGroup.class));
        DynamoDbIndex<DDBGroup> userGroupsIndex = groupsTable.index(USER_GROUPS_INDEX);

        return new DDBGroupDAO(groupsTable, userGroupsIndex);
    }

    @Provides
    @Reusable
    static StatsForUpcomingGamesDAO statsForUpcomingGamesDAO(S3Client s3Client) {
        String gamesBucketName = System.getenv(GAMES_BUCKET_NAME_KEY);
        return new S3StatsForUpcomingGameDAO(s3Client, gamesBucketName);
    }
}
