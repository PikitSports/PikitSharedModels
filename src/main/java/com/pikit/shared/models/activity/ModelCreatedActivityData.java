package com.pikit.shared.models.activity;

import com.pikit.shared.dao.ddb.model.DDBModel;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ModelCreatedActivityData extends ActivityData {
    private ActivityType activityType;
    private DDBModel modelCreated;
}
