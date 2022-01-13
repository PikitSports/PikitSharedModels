package com.pikit.shared.models;

import com.pikit.shared.enums.League;
import lombok.*;

import java.util.ArrayList;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatConfiguration {
    private ArrayList<Stat> stats;
    private League league;
    private int startRange;
    private int endRange;
    private String statDescription;
    private boolean publicStat;
}
