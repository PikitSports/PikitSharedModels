package com.pikit.shared.models;

import lombok.*;

import java.util.HashMap;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModelPerformance {
    private HashMap<String, List<BetProfitability>> individualSeasonProfitability;
    private List<BetProfitability> overallProfitability;
    private List<TopGameData> latestGames;
}