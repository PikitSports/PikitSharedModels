package com.pikit.shared.dagger;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module
public class DynamoClientModule {
    @Provides
    @Reusable
    public DynamoDB getDynamoClient() {
        return new DynamoDB(AmazonDynamoDBClientBuilder.defaultClient());
    }
}
