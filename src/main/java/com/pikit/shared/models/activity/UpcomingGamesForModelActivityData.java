package com.pikit.shared.models.activity;

import com.pikit.shared.dao.ddb.model.DDBModel;
import com.pikit.shared.models.UpcomingGameThatMeetsModel;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UpcomingGamesForModelActivityData extends ActivityData {
    private ActivityType activityType;
    private DDBModel model;
    private List<UpcomingGameThatMeetsModel> upcomingGames;
}
