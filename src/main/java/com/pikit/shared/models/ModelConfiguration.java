package com.pikit.shared.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pikit.shared.enums.Bet;
import com.pikit.shared.enums.BetType;
import com.pikit.shared.enums.League;
import com.pikit.shared.enums.ModelTimeRange;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModelConfiguration {
    private League league;
    private String modelDescription;
    private List<ModelRequirement> modelRequirements;
    private List<Bet> betsTaken;
    private BetType betType;
    private List<String> seasonsStored;
    private ModelTimeRange timeRange;
}
