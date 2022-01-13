package com.pikit.shared.models;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamMetadataList {
    private List<TeamMetadata> teams;
}
