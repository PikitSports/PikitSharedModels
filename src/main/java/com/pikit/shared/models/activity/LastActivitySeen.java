package com.pikit.shared.models.activity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LastActivitySeen {
    private String activityId;
    private Long activityTimestamp;
    private String user;
}
