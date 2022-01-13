package com.pikit.shared.models;

import com.pikit.shared.enums.League;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SNSModel {
    private String modelId;
    private String userCreatedBy;
    private String modelConfiguration;
    private long creationTimestamp;
    private League league;
}
