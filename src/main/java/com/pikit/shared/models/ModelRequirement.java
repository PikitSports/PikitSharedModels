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
    private Boolean forOpponent;
    private String description;
    private String statUsed;
    private boolean comparingOpponent;
    private Double comparisonValue;
    private ConditionRequirement conditionRequirement;
    private String acceptedStringValue;
    private Boolean expectedBooleanValue;
    private Integer variant;
}
