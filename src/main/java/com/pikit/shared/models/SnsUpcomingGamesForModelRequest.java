package com.pikit.shared.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pikit.shared.enums.League;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SnsUpcomingGamesForModelRequest {
    private SNSModel model;
    private League league;
}
