package com.pikit.shared.models.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties
@JsonTypeInfo(use= JsonTypeInfo.Id.NAME, property = "activityType")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = ModelCreatedActivityData.class, name = "MODEL_CREATED"),
        @JsonSubTypes.Type(value = ModelUpdatedActivityData.class, name = "MODEL_UPDATED"),
        @JsonSubTypes.Type(value = UpcomingGamesForModelActivityData.class, name = "UPCOMING_GAMES_FOR_MODEL")
})
public abstract class ActivityData {
}
