package com.pikit.shared.models;

import com.pikit.shared.enums.Bet;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BetProfitability {
    private int wins;
    private int losses;
    private int pushes;
    private double units;
}
