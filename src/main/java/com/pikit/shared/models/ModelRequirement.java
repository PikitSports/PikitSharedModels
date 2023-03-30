package com.pikit.shared.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelRequirement {
    private boolean forOpponent;
    private String description;
    private FilterType filterType;
    private String statUsed;
    private boolean comparingOpponent;
    private double comparisonValue;
    private ConditionRequirement conditionRequirement;
    private List<String> acceptedStringValues;
    private boolean expectedBooleanValue;
}
