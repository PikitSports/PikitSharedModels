package com.pikit.shared.models;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RecentGame {
    private String homeTeam;
    private String awayTeam;
    private String homeTeamScore;
    private String awayTeamScore;
    private String gameDate;
    private String gameTime;
    private Boolean gameCompleted;
    private Boolean gameStarted;
}
