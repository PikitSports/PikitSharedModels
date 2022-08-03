package com.pikit.shared.dagger;

import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Table;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

import javax.inject.Named;

@Module
public class DynamoIndexClientModule {

    private static final String MODEL_INDEX_NAME = "modelId-index";

    @Provides
    @Reusable
    @Named("modelIdIndex")
    public Index getModelIdIndex(@Named("upcomingGamesTable") Table table) {
        return table.getIndex(MODEL_INDEX_NAME);
    }
}
