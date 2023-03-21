package com.pikit.shared.dao.ddb.converter;

import com.pikit.shared.models.ModelConfiguration;
import lombok.SneakyThrows;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class ModelConfigurationConverter implements AttributeConverter<ModelConfiguration> {
    @SneakyThrows
    @Override
    public AttributeValue transformFrom(ModelConfiguration modelConfiguration) {
        return AttributeValue.builder()
                .s(SlickPicksConverter.toJSONString(modelConfiguration))
                .build();
    }

    @SneakyThrows
    @Override
    public ModelConfiguration transformTo(AttributeValue attributeValue) {
        return SlickPicksConverter.createFromJSONString(attributeValue.s(), ModelConfiguration.class);
    }

    @Override
    public EnhancedType<ModelConfiguration> type() {
        return EnhancedType.of(ModelConfiguration.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
