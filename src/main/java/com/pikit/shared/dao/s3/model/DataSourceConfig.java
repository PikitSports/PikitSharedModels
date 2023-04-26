package com.pikit.shared.dao.s3.model;

import com.pikit.shared.enums.League;
import com.pikit.shared.models.StatMetadata;
import lombok.*;

import java.util.HashMap;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DataSourceConfig {
    private League league;
    private String startSeason;
    private String endSeason;
    private HashMap<String, StatMetadata> statsAvailable;
}
