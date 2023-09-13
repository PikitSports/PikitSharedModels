package com.pikit.shared.models;

import com.pikit.shared.enums.BetResult;
import lombok.*;

@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameBetResult {
    private BetResult result;
    private Double units;
}
