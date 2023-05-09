package com.pikit.shared.dao.ddb.converter;

import lombok.SneakyThrows;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class MapAttributeConverter implements AttributeConverter<Map> {
    @SneakyThrows
    @Override
    public AttributeValue transformFrom(Map map) {
        return AttributeValue.builder()
                .s(SlickPicksConverter.toJSONString(map))
                .build();
    }

    @SneakyThrows
    @Override
    public Map transformTo(AttributeValue attributeValue) {
        return SlickPicksConverter.createFromJSONString(attributeValue.s(), Map.class);
    }

    @Override
    public EnhancedType<Map> type() {
        return EnhancedType.of(Map.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
