package com.pikit.shared.models.ddb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pikit.shared.enums.League;
import com.pikit.shared.models.Stat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DDBStat {
    private String statName;
    private League league;
    private long creationTimestamp;
    private Stat stat;
    private String userCreatedBy;
    private String statDescription;
    private boolean publicStat;

    public void setStat(String stat) throws IOException {
        this.stat = new ObjectMapper().readValue(stat, Stat.class);
    }
}
