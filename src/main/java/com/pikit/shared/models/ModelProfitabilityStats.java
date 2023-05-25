package com.pikit.shared.models;

import lombok.*;

@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModelProfitabilityStats {
    private Double last10Games;
    private Double last50Games;
    private Double last100Games;
}
