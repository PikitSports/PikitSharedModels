package com.pikit.shared.models;

import com.pikit.shared.enums.StatIdentifier;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatVariableInvolved {
    private String variableName;
    private StatIdentifier statIdentifier;
    private String value;
}
