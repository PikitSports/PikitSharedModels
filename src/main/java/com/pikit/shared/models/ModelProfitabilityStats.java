package com.pikit.shared.models;

import lombok.*;

@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModelProfitabilityStats {
    private Double last10GamesUnits;
    private Double last50GamesUnits;
    private Double last100GamesUnits;
    private Double last10GamesWinPercentage;
    private Double last50GamesWinPercentage;
    private Double last100GamesWinPercentage;
}
