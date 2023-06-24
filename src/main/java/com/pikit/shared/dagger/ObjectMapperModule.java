package com.pikit.shared.dagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module
public class ObjectMapperModule {
    @Provides
    @Reusable
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper().configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    }
}
