package com.pikit.shared.models;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatisticPercentiles {
    private double p10;
    private double p20;
    private double p30;
    private double p40;
    private double p50;
    private double p60;
    private double p70;
    private double p80;
    private double p90;
}
