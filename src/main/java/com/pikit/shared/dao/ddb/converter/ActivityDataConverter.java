package com.pikit.shared.dao.ddb.converter;

import com.pikit.shared.models.activity.ActivityData;
import lombok.SneakyThrows;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class ActivityDataConverter implements AttributeConverter<ActivityData> {
    @Override
    @SneakyThrows
    public AttributeValue transformFrom(ActivityData activityData) {
        return AttributeValue.builder()
                .s(SlickPicksConverter.toJSONString(activityData))
                .build();
    }

    @Override
    @SneakyThrows
    public ActivityData transformTo(AttributeValue attributeValue) {
        return SlickPicksConverter.createFromJSONString(attributeValue.s(), ActivityData.class);
    }

    @Override
    public EnhancedType<ActivityData> type() {
        return EnhancedType.of(ActivityData.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
