package com.pikit.shared.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pikit.shared.enums.Bet;
import com.pikit.shared.enums.BetResult;
import lombok.*;

import java.util.HashMap;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopGameData {
    private String homeTeam;
    private String awayTeam;
    private String gameDate;
    private HashMap<Bet, BetResult> bets;
    @JsonIgnore
    private String numGameForDay;
}
