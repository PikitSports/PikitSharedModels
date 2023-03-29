package com.pikit.shared.models;

import com.pikit.shared.enums.DataType;
import com.pikit.shared.enums.FilterType;
import com.pikit.shared.enums.StatIdentifier;
import com.pikit.shared.enums.VariableType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatMetadata {
    private StatIdentifier statIdentifier; //This tells us where to find the stat in the game JSON object
    private DataType dataType;
    private VariableType variableType;
    private FilterType filterType; //This tells us how we should process a model requirement using this stat.
}
