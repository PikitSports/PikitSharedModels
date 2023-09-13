package com.pikit.shared.models;

import com.pikit.shared.enums.Bet;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModelPerformance {
    private Map<String, Map<Bet, BetProfitability>> individualSeasonProfitability;
    private Map<String, List<ModelStatistic>> individualSeasonStatistics;
    private Map<Bet, BetProfitability> overallProfitability;
    private List<ModelStatistic> overallStatistics;
    private List<TopGameData> latestGames;
}