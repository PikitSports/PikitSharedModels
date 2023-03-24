package com.pikit.shared.dao.ddb.converter;

import com.pikit.shared.models.GameThatMeetsModel;
import lombok.SneakyThrows;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.List;

public class GamesThatMeetModelConverter implements AttributeConverter<List<GameThatMeetsModel>> {
    @SneakyThrows
    @Override
    public AttributeValue transformFrom(List<GameThatMeetsModel> gameThatMeetsModelList) {
        List<AttributeValue> listItems = new ArrayList<>();
        for (GameThatMeetsModel game: gameThatMeetsModelList) {
            listItems.add(AttributeValue.builder()
                    .s(SlickPicksConverter.toJSONString(game))
                    .build());
        }

        return AttributeValue.builder()
                .l(listItems)
                .build();
    }

    @SneakyThrows
    @Override
    public List<GameThatMeetsModel> transformTo(AttributeValue attributeValue) {
        List<GameThatMeetsModel> gamesList = new ArrayList<>();
        List<AttributeValue> storedList = attributeValue.l();

        for (AttributeValue value: storedList) {
            gamesList.add(SlickPicksConverter.createFromJSONString(value.s(), GameThatMeetsModel.class));
        }

        return gamesList;
    }

    @Override
    public EnhancedType<List<GameThatMeetsModel>> type() {
        return new EnhancedType<List<GameThatMeetsModel>>(){};
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.L;
    }
}
