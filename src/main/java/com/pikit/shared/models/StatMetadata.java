package com.pikit.shared.models;

import com.pikit.shared.enums.DataType;
import com.pikit.shared.enums.StatIdentifier;
import com.pikit.shared.enums.VariableType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatMetadata {
    private StatIdentifier statIdentifier;
    private DataType dataType;
    private VariableType variableType;
}
