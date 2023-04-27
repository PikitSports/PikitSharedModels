package com.pikit.shared.models;

import lombok.*;

@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BettingStats {
    private String homeTeamMoneyLine;
    private String homeTeamSpread;
    private String awayTeamMoneyLine;
    private String awayTeamSpread;
    private String overUnder;
}
