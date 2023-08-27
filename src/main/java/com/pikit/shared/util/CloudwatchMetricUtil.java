package com.pikit.shared.util;

import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CloudwatchMetricUtil {
    private static final String NAMESPACE = "PickVision";

    public static PutMetricDataRequest buildCloudwatchRequest(String metricName,
                                                           double value,
                                                           String unit,
                                                           Map<String, String> dimensionMap) {

        MetricDatum metric = new MetricDatum()
                .withMetricName(metricName)
                .withUnit(unit)
                .withValue(value);

        if (dimensionMap != null) {
            List<Dimension> dimensions = new ArrayList<>();
            dimensionMap.forEach((key, mapping) -> {
                dimensions.add(new Dimension()
                        .withName(key)
                        .withValue(mapping));
            });
            metric.withDimensions(dimensions);
        }

        return new PutMetricDataRequest()
                .withNamespace(NAMESPACE)
                .withMetricData(metric);

    }
}
