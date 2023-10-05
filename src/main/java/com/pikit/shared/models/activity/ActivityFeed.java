package com.pikit.shared.models.activity;

import com.pikit.shared.dao.ddb.model.DDBActivity;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ActivityFeed {
    private List<DDBActivity> activityList;
    private LastActivitySeen lastActivitySeen;
}
