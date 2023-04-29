package com.pikit.shared.dao.ddb.model;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DDBUser {
    private static final String USER_ID_ATTRIBUTE = "userId";
    private static final String EMAIL_ATTRIBUTE = "email";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String PHONE_NUMBER_ATTRIBUTE = "phoneNumber";
    private static final String BIRTHDAY_ATTRIBUTE = "birthday";

    @Getter(onMethod_ = {
            @DynamoDbAttribute(USER_ID_ATTRIBUTE),
            @DynamoDbPartitionKey
    })
    private String userId;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(NAME_ATTRIBUTE)
    })
    private String name;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(EMAIL_ATTRIBUTE)
    })
    private String email;

    @Getter(onMethod_ = {
            @DynamoDbAttribute(PHONE_NUMBER_ATTRIBUTE)
    })
    private String phoneNumber;
}