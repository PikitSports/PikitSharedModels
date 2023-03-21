package com.pikit.shared.dao.ddb.converter;

import com.pikit.shared.models.ModelPerformance;
import lombok.SneakyThrows;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class ModelPerformanceConverter implements AttributeConverter<ModelPerformance> {
    @SneakyThrows
    @Override
    public AttributeValue transformFrom(ModelPerformance modelPerformance) {
        return AttributeValue.builder()
                .s(SlickPicksConverter.toJSONString(modelPerformance))
                .build();
    }

    @SneakyThrows
    @Override
    public ModelPerformance transformTo(AttributeValue attributeValue) {
        return SlickPicksConverter.createFromJSONString(attributeValue.s(), ModelPerformance.class);
    }

    @Override
    public EnhancedType<ModelPerformance> type() {
        return EnhancedType.of(ModelPerformance.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
