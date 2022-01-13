package com.pikit.shared.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pikit.shared.enums.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Stat {
    String statName;
    StatType statType;
    Boolean accumulatedStat;
    AccumulationType accumulationType;
    List<StatVariableInvolved> variablesInvolved;
    ComparisonOperation comparisonOperation;
    ComparisonReturnValue comparisonReturnValue;
    Accumulate accumulate;
    DataType dataType;
    ConditionRequirement conditionRequirement;
    double conditionValue;
    VariableType variableType; // stats will sometimes have variable types if they are indicating bet variables like TOTAL_SCORE or GAME_WINNER
    String statDescription;
    boolean publicStat;

    public boolean isAccumulatedStat() {
        return this.accumulatedStat;
    }
}
