package com.pikit.shared.models;

import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
/*
This POJO represents the output of a model run and all of the data we will get from that run.
 */
public class ModelRun {
    TreeMap<String, List<GameThatMeetsModel>> gamesThatMeetModel;
    private HashMap<String, List<BetProfitability>> individualSeasonProfitability;
    private List<BetProfitability> overallProfitability;
    private List<String> dataSourceHeaders;
}
