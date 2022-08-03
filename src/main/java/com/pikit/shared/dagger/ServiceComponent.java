package com.pikit.shared.dagger;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pikit.shared.util.PikitSharedUtils;
import dagger.Component;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Component(modules = {
        ObjectMapperModule.class,
        DynamoClientModule.class,
        DynamoTableClientModule.class,
        DynamoIndexClientModule.class,
        SharedUtilModule.class,
        S3ClientModule.class
})
public interface ServiceComponent {
    ObjectMapper getObjectMapper();
    DynamoDB getDynamoClient();
    AmazonS3 getS3Client();
    PikitSharedUtils getSharedUtil();

    //Dynamo Tables
    @Named("modelsTable")
    Table getModelsTable();
    @Named("nflGamesTable")
    Table getNflGamesTable();
    @Named("nbaGamesTable")
    Table getNbaGamesTable();
    @Named("gamesThatMeetModelTable")
    Table getGamesThatMeetModelTable();
    @Named("upcomingGamesTable")
    Table getUpcomingGamesTable();
    @Named("modelIdIndex")
    Index getModelIdIndex();
}
