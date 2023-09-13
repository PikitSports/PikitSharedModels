package com.pikit.shared.models;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModelStatistic {
    private String statisticName;
    private StatisticPercentiles percentiles;
}
