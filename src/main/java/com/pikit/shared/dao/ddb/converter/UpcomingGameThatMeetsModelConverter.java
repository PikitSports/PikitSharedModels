package com.pikit.shared.dao.ddb.converter;

import com.pikit.shared.models.UpcomingGameThatMeetsModel;
import lombok.SneakyThrows;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class UpcomingGameThatMeetsModelConverter implements AttributeConverter<UpcomingGameThatMeetsModel> {

    @SneakyThrows
    @Override
    public AttributeValue transformFrom(UpcomingGameThatMeetsModel gameThatMeetsModel) {
        return AttributeValue.builder()
                .s(SlickPicksConverter.toJSONString(gameThatMeetsModel))
                .build();
    }

    @SneakyThrows
    @Override
    public UpcomingGameThatMeetsModel transformTo(AttributeValue attributeValue) {
        return SlickPicksConverter.createFromJSONString(attributeValue.s(), UpcomingGameThatMeetsModel.class);
    }

    @Override
    public EnhancedType<UpcomingGameThatMeetsModel> type() {
        return EnhancedType.of(UpcomingGameThatMeetsModel.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
