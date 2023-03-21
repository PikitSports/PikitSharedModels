package com.pikit.shared.dagger;

import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pikit.shared.dao.ddb.DDBGamesThatMeetModelDAO;
import com.pikit.shared.dao.ddb.DDBModelDAO;
import com.pikit.shared.dao.ddb.DDBUpcomingGamesDAO;
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
        S3ClientModule.class,
        DAOModule.class,
})
public interface ServiceComponent {
    ObjectMapper getObjectMapper();
    AmazonS3 getS3Client();
    PikitSharedUtils getSharedUtil();
    DDBModelDAO modelDAO();
    DDBGamesThatMeetModelDAO gamesThatMeetModelDAO();
    DDBUpcomingGamesDAO upcomingGamesDAO();

    //Dynamo Tables
    @Named("nflGamesTable")
    Table getNflGamesTable();
    @Named("nbaGamesTable")
    Table getNbaGamesTable();
}
