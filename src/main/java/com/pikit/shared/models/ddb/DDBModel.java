package com.pikit.shared.models.ddb;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pikit.shared.enums.League;
import com.pikit.shared.models.ModelConfiguration;
import com.pikit.shared.models.ModelPerformance;
import com.pikit.shared.models.TopGameData;
import lombok.*;

import java.io.IOException;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DDBModel {
    private String modelId;
    private String userCreatedBy;
    private ModelConfiguration modelConfiguration;
    private long creationTimestamp;
    private League league;
    private List<String> gamesStored;
    private ModelPerformance modelPerformance;
    private List<TopGameData> top3Games;
    private Boolean alertsEnabled;

    public void setModelConfiguration(String modelConfiguration) throws IOException {
        this.modelConfiguration = new ObjectMapper().readValue(modelConfiguration, ModelConfiguration.class);
    }

    public void setModelPerformance(String modelPerformance) throws IOException {
        this.modelPerformance = new ObjectMapper().readValue(modelPerformance, ModelPerformance.class);
    }

    public void setTop3Games(String top3Games) throws IOException {
        this.top3Games = new ObjectMapper().readValue(top3Games, new TypeReference<List<TopGameData>>(){});
    }
}
