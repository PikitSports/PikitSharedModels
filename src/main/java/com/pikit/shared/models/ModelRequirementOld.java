package com.pikit.shared.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pikit.shared.enums.ComparisonOperation;
import com.pikit.shared.enums.ConditionRequirement;
import com.pikit.shared.enums.FilterType;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModelRequirementOld {
    private boolean forOpponent;
    private String description;
    private FilterType filterType;
    private List<String> statsUsed;
    private ComparisonOperation comparisonOperation;
    private ConditionRequirement conditionRequirement;
    private double conditionValue;
    private List<String> acceptedExactValues;
    private String acceptedStartRangeValue;
    private String acceptedEndRangeValue;
    private boolean statExpectedValue;
}
