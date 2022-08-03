package com.pikit.shared.dagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module
public class ObjectMapperModule {
    @Provides
    @Reusable
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
}
