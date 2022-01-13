package com.pikit.shared.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pikit.shared.enums.Region;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamMetadata {
    private String teamName;
    private String division;
    private String conference;
    private Region region;
}
