package com.pikit.shared.dagger;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pikit.shared.client.S3Client;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module
public class S3ClientModule {
    @Provides
    @Reusable
    public AmazonS3 getAmazonS3() {
        return AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
    }

    @Provides
    @Reusable
    public S3Client getS3Client(AmazonS3 amazonS3, ObjectMapper objectMapper) {
        return new S3Client(amazonS3, objectMapper);
    }
}
