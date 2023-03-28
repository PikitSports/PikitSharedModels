package com.pikit.shared.dagger;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

import javax.inject.Named;

@Module
public class DynamoTableClientModule {

    private static final String MODELS_TABLE_NAME = "Pikit_Models";
    private static final String NFL_TABLE_NAME = "Pikit_NFL_Games";
    private static final String NBA_TABLE_NAME = "Pikit_NBA_Games";
    private static final String GAMES_THAT_MEET_MODEL_TABLE_NAME = "Pikit_Games_That_Meet_Model";
    private static final String UPCOMING_GAMES_TABLE_NAME = "Pikit_Upcoming_Games_That_Meet_Model";

    @Provides
    @Reusable
    @Named("modelsTable")
    public Table getModelsTable(DynamoDB dynamoClient) {
        return dynamoClient.getTable(MODELS_TABLE_NAME);
    }

    @Provides
    @Reusable
    @Named("nflGamesTable")
    public Table getNflGamesTable(DynamoDB dynamoClient) {
        return dynamoClient.getTable(NFL_TABLE_NAME);
    }

    @Provides
    @Reusable
    @Named("nbaGamesTable")
    public Table getNbaGamesTable(DynamoDB dynamoClient) {
        return dynamoClient.getTable(NBA_TABLE_NAME);
    }

    @Provides
    @Reusable
    @Named("gamesThatMeetModelTable")
    public Table getGamesThatMeetModelTable(DynamoDB dynamoClient) {
        return dynamoClient.getTable(GAMES_THAT_MEET_MODEL_TABLE_NAME);
    }

    @Provides
    @Reusable
    @Named("upcomingGamesTable")
    public Table getUpcomingGamesTable(DynamoDB dynamoClient) {
        return dynamoClient.getTable(UPCOMING_GAMES_TABLE_NAME);
    }
}
