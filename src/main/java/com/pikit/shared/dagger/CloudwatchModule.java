package com.pikit.shared.dagger;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.pikit.shared.client.CloudwatchClient;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module
public class CloudwatchModule {
    @Provides
    @Reusable
    AmazonCloudWatch cloudWatch() {
        return AmazonCloudWatchClientBuilder.defaultClient();
    }

    @Provides
    @Reusable
    CloudwatchClient cloudwatchClient(AmazonCloudWatch cloudWatch) {
        return new CloudwatchClient(cloudWatch);
    }
}
