package com.pikit.shared.dao.ddb.converter;

import com.pikit.shared.models.GamesThatMeetModel;
import lombok.SneakyThrows;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class GamesThatMeetModelConverter implements AttributeConverter<GamesThatMeetModel> {

    @SneakyThrows
    @Override
    public AttributeValue transformFrom(GamesThatMeetModel gamesThatMeetModel) {
        return AttributeValue.builder()
                .s(SlickPicksConverter.toJSONString(gamesThatMeetModel))
                .build();
    }

    @SneakyThrows
    @Override
    public GamesThatMeetModel transformTo(AttributeValue attributeValue) {
        return SlickPicksConverter.createFromJSONString(attributeValue.s(), GamesThatMeetModel.class);
    }

    @Override
    public EnhancedType<GamesThatMeetModel> type() {
        return EnhancedType.of(GamesThatMeetModel.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
