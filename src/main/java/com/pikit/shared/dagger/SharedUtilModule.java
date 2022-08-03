package com.pikit.shared.dagger;

import com.pikit.shared.util.PikitSharedUtils;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module
public class SharedUtilModule {
    @Provides
    @Reusable
    public PikitSharedUtils getSharedUtils() {
        return new PikitSharedUtils();
    }
}
