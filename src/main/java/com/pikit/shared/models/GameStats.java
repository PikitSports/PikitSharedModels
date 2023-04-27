package com.pikit.shared.models;

import lombok.*;

@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameStats {
    private String homeTeam;
    private String awayTeam;
    private String gameDate;
    private int numGameForDay;
}
